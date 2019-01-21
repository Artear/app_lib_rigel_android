package com.artear.rigel

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.view.Menu
import android.widget.ImageView
import android.widget.LinearLayout
import com.artear.ui.base.ArtearActionBarProperties
import com.artear.ui.extensions.configCoordinatorStatusBar
import com.artear.ui.extensions.setActionBar
import com.artear.ui.extensions.setBarsBackground
import com.artear.ui.interfaces.ArtearActionBarOwner
import com.artear.ui.views.BaseActionBarView
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*


abstract class NavigationActivity : AppCompatActivity(), ArtearActionBarOwner,
        BaseActionBarView.OnActionImageClickListener {

    var menu: Menu? = null

    companion object {
        const val FRAGMENT_TAG = "fragment %s"
        const val NAVIGATION_H_STACK = "navigation_h_stack"
    }

    protected abstract var navigationProvider: NavigationProvider

    private var navigationHorizontalStack = Stack<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        setActionBar(mainToolbar, baseActionBar, this)
        configCoordinatorStatusBar(root, status)
        setBarsBackground(R.color.white, mainAppBarLayout, status)

        restoreNavigationHorizontalStack(savedInstanceState)

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (navigationHorizontalStack.isEmpty()) {
            bottomNavigationView.selectedItemId = navigationProvider.mainMenu
        } else {
            getFragmentByPosition(navigationHorizontalStack.peek())?.let {
                updateActionBarFromFragment(it)
            }
        }

        bottomNavigationView.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)
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
                    val currentFragment = getFragmentByPosition(navigationHorizontalStack.peek())
                    currentFragment?.onReselected()
                    //TODO
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
        getFragmentByPosition(navigationHorizontalStack.peek())?.let {
            val artearFragment = it.childFragmentManager.fragments.last() as ActionBarFragment
            if (idFragment == artearFragment.id) {
                //TODO
                //mainAppBarLayout.setExpanded(true)

                cleanActionBar(baseActionBar)

                actionBarProperties.flowIconResource?.let { res ->
                    baseActionBar.setActionImageVisibility(true)
                    baseActionBar.setActionImage(res)
                }

                actionBarProperties.title?.let { title ->
                    baseActionBar.setTitleContent(title)
                }

                baseActionBar.toggleLogoTextVisibility((actionBarProperties.titleContentDrawable != null))

                buttonsLayout().removeAllViewsInLayout()
                actionBarProperties.actionsMenuResourceId?.let { menuResourceId ->
                    loadMenu(menuResourceId)
                }
            }
        }
    }

    private fun cleanActionBar(baseActionBar: BaseActionBarView) {
        baseActionBar.toggleLogoTextVisibility(false)
        baseActionBar.setTitleContent("")
        baseActionBar.setActionImageVisibility(false)
    }

    //TODO
//    private fun findFragment(idFragment: String): ArticleFragment? {
//        navigationHorizontalStack.forEach { horizontal ->
//            getFragmentByPosition(horizontal)?.let {
//                it.childFragmentManager.fragments.forEach { child ->
//                    if (child is ArticleFragment) {
//                        if (idFragment == child.id) {
//                            return child
//                        }
//                    }
//                }
//            }
//        }
//        return null
//    }

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
        var fragment = getFragmentByPosition(position)
        if (fragment != null) {
            if (fragment.isAdded) updateActionBarFromFragment(fragment)
            transaction.show(fragment)
        } else {
            fragment = MainFragment.newInstance(navigationProvider.values[position])
            transaction.add(R.id.main_fragment_container, fragment,
                    String.format(FRAGMENT_TAG, position))
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

            val fragment = getFragmentByPosition(navigationHorizontalStack.peek())
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

    private fun getFragmentByPosition(position: Int): MainFragment? {
        val fragmentTag = String.format(FRAGMENT_TAG, position)
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
        getFragmentByPosition(navigationHorizontalStack.peek())?.actionClicked(actionId)
    }

    private fun buttonsLayout(): LinearLayout {
        return baseActionBar.findViewById(R.id.buttonsLayout)
    }


}