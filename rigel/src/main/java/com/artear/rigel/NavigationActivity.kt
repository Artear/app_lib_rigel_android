/*
 * Copyright 2019 Artear S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.artear.rigel

import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.artear.rigel.MainFragment.Companion.MAIN_FRAGMENT_TAG
import com.artear.rigel.MainFragment.Companion.SECTION
import com.artear.ui.base.ArtearActionBarProperties
import com.artear.ui.base.ArtearFragment
import com.artear.ui.extensions.configCoordinatorStatusBar
import com.artear.ui.extensions.setActionBar
import com.artear.ui.extensions.setBarsBackground
import com.artear.ui.interfaces.ArtearActionBarOwner
import com.artear.ui.views.BaseActionBarView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.navigation_activity.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Extend this class to make your application a single activity application.
 *
 * The main aim of this library is make simplest the navigation. Center in one an only one activity,
 * with many sections in a bottom navigation and control the back stack. Then you must to use a few
 * classes to make it possible.
 *
 * On the one hand you must have an base activity of your project extend of this and override
 * the [NavigationProvider]. An enum maybe can a way to do that and you must to create a fragment for
 * each section. On the other hand for inflate your menu sections must implement
 * [onBottomNavigationViewCreated].
 *
 * If you need your own [MainFragment] you also can extend them and override [getMainFragmentClass].
 *
 * The activity hast a navigation horizontal stack that control that flow. Make a [MainFragment]
 * for each section and is the owner of unique toolbar in the app. See: *ArtearActionBarOwner*
 * in uiview library.
 *
 * On back pressed first check the vertical navigation, and if the section fragment has not more
 * that one the logic find the last fragment in the horizontal stack and show them.
 *
 * The flow is lifecycle safe and if system destroy the activity the stack will be restored too.
 *
 *
 * @see MainFragment
 * @author David Tolchinsky
 *
 */
abstract class NavigationActivity : AppCompatActivity(), ArtearActionBarOwner,
        BaseActionBarView.OnActionImageClickListener {

    private var menu: Menu? = null

    companion object {
        const val NAVIGATION_H_STACK = "navigation_h_stack"
    }

    protected abstract var navigationProvider: NavigationProvider

    private var navigationHorizontalStack = Stack<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.navigation_activity)

        setActionBar(mainToolbar, baseActionBarView, this)
        configCoordinatorStatusBar(root, status)
        setBarsBackground(R.color.white, mainAppBarLayout, status)

        restoreNavigationHorizontalStack(savedInstanceState)

        bottomNavigationView?.let { onBottomNavigationViewCreated(it) }

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (navigationHorizontalStack.isEmpty()) {
            bottomNavigationView.selectedItemId = navigationProvider.mainMenu
        } else {
            getMainFragmentByPosition(navigationHorizontalStack.peek())?.let {
                updateActionBarFromFragment(it)
            }
        }

        bottomNavigationView.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)
    }

    /**
     * Needs override for inflate your custom menu.
     */
    open fun onBottomNavigationViewCreated(bottomNavigationView: BottomNavigationView) {
    }

    /**
     * Override this if you need your own base fragment. Must be extend [MainFragment]
     */
    open fun getMainFragmentClass(): KClass<out MainFragment> {
        return MainFragment::class
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(NAVIGATION_H_STACK, navigationHorizontalStack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreNavigationHorizontalStack(savedInstanceState)
    }

    private fun restoreNavigationHorizontalStack(savedInstanceState: Bundle?) {
        if (navigationHorizontalStack.isEmpty()) {
            savedInstanceState?.getSerializable(NAVIGATION_H_STACK)?.let {
                val arrayList = it as AbstractList<*>
                for (value in arrayList) {
                    if (value is Int) {
                        navigationHorizontalStack.push(value)
                    }
                }
            }
        }
    }

    private val mOnNavigationItemReselectedListener =
            BottomNavigationView.OnNavigationItemReselectedListener {
                if (!navigationHorizontalStack.empty()) {
                    val currentFragment = getMainFragmentByPosition(navigationHorizontalStack.peek())
                    currentFragment?.onReselected()
                    //mainAppBarLayout.setExpanded(true)
                }
            }

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                val position = getPositionItemId(item.itemId)
                if (position == -1)
                    return@OnNavigationItemSelectedListener false
                showFragment(position)
                true
            }

    private fun updateActionBarFromFragment(fragment: Fragment) {
        val artearFragment = fragment.childFragmentManager.fragments.last() as ActionBarFragment
        artearFragment.updateActionBar()
    }

    @Synchronized
    override fun updateActionBar(actionBarProperties: ArtearActionBarProperties, idFragment: String,
                                 updateFragment: Boolean) {
        getMainFragmentByPosition(navigationHorizontalStack.peek())?.let {
            val artearFragment = it.childFragmentManager.fragments.last() as ActionBarFragment
            if (idFragment == artearFragment.id) {
                //mainAppBarLayout.setExpanded(true)

                cleanActionBar()

                actionBarProperties.flowIconResource?.let { res ->
                    baseActionBarView.setActionImageVisibility(true)
                    baseActionBarView.setActionImage(res)
                }

                actionBarProperties.title?.let { title ->
                    baseActionBarView.setTitleContent(title)
                }

                baseActionBarView.toggleLogoTextVisibility((actionBarProperties.titleContentDrawable != null))

                buttonsLayout().removeAllViewsInLayout()
                actionBarProperties.actionsMenuResourceId?.let { menuResourceId ->
                    loadMenu(menuResourceId)
                }
            }
        }
    }

    private fun cleanActionBar() {
        baseActionBarView.toggleLogoTextVisibility(false)
        baseActionBarView.setTitleContent("")
        baseActionBarView.setActionImageVisibility(false)
    }

    protected fun findFragment(idFragment: String): ArtearFragment? {
        navigationHorizontalStack.forEach { horizontal ->
            getMainFragmentByPosition(horizontal)?.run {
                return childFragmentManager.fragments.find { child ->
                    child is ArtearFragment && idFragment == child.id
                } as ArtearFragment
            }
        }
        return null
    }

    /**
     * When user click action image in action bar
     */
    override fun onClickActionImage() {
        onBackPressed()
    }

    private fun getPositionItemId(itemId: Int): Int {
        return navigationProvider.values.first { it.idMenu == itemId }.position
    }

    private fun showFragment(position: Int) {

        navigationHorizontalStack.remove(position)
        navigationHorizontalStack.push(position)

        val transaction = supportFragmentManager.beginTransaction()
        var fragment = getMainFragmentByPosition(position)
        if (fragment != null) {
            if (fragment.isAdded) updateActionBarFromFragment(fragment)
            transaction.show(fragment)
        } else {
            fragment = getMainFragmentClass().createInstance().apply {
                arguments = Bundle().apply {
                    putParcelable(SECTION, navigationProvider.values[position])
                }
            }
            transaction.add(R.id.main_fragment_container, fragment,
                    String.format(MAIN_FRAGMENT_TAG, position))
        }
        supportFragmentManager.fragments.forEach {
            if (it != fragment && !it.isHidden) {
                transaction.hide(it)
            }
        }

        bottomNavigationView.menu.getItem(position).isChecked = true

        transaction.commit()
    }

    override fun onBackPressed() {

        if (!navigationHorizontalStack.empty()) {

            val fragment = getMainFragmentByPosition(navigationHorizontalStack.peek())
            fragment?.let {
                //Check vertical navigation
                if (it.childFragmentManager.backStackEntryCount > 1) {
                    fragment.childFragmentManager.popBackStackImmediate()
                    return
                }
            }
        }

        if (navigationHorizontalStack.empty() || navigationHorizontalStack.size == 1) {
            finish()
            return
        } else {
            //Here need to change horizontal fragment
            navigationHorizontalStack.pop()
            showFragment(navigationHorizontalStack.peek())
        }

    }

    /**
     * @return The [MainFragment] in horizontal stack for that position
     */
    private fun getMainFragmentByPosition(position: Int): MainFragment? {
        val fragmentTag = String.format(MAIN_FRAGMENT_TAG, position)
        return supportFragmentManager.findFragmentByTag(fragmentTag) as? MainFragment
    }

    private fun loadMenu(menuResourceId: Int) {

        val p = PopupMenu(this, root)
        menu = p.menu
        menuInflater.inflate(menuResourceId, menu)

        menu?.let { menu ->
            var i = 0
            while (i < menu.size()) {

                val action = ImageView(this).apply {

                    val item = menu.getItem(i)
                    // Set an image for ImageView
                    setImageDrawable(item.icon)

                    // Create layout parameters for ImageView
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        marginStart = resources.getDimensionPixelSize(R.dimen.actions_margin)
                    }

                    // Finally, add the ImageView to layout
                    isClickable = true
                    setOnClickListener { actionClicked(item.itemId) }
                }

                buttonsLayout().addView(action)

                i++
            }
        }
    }

    private fun actionClicked(actionId: Int) {
        getMainFragmentByPosition(navigationHorizontalStack.peek())?.actionClicked(actionId)
    }

    private fun buttonsLayout(): LinearLayout {
        return baseActionBarView.findViewById(R.id.buttonsLayout)
    }


}