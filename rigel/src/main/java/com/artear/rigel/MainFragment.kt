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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artear.rigel.extensions.getChildActiveFragment
import com.artear.rigel.extensions.ifNull
import com.artear.ui.extensions.showToast


open class MainFragment : Fragment() {

    companion object {
        internal const val SECTION = "section"
        const val MAIN_FRAGMENT_TAG = "main_f_%s"
    }

    protected var section: NavigationSection? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    private val onBackStackListener = {
        if (childFragmentManager.backStackEntryCount > 0) {
            val artearFragment = childFragmentManager.fragments.last() as? ActionBarFragment
            artearFragment?.updateActionBar()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        section = arguments?.let {
            it.getParcelable(SECTION) as NavigationSection
        }.ifNull {
            savedInstanceState?.getParcelable(SECTION)
        }

        section?.let {

            val tag = getBaseId(it)
            var fragment: Fragment?

            savedInstanceState?.let {
                childFragmentManager.findFragmentByTag(tag)
            }.ifNull {
                childFragmentManager.beginTransaction().let { transaction ->
                    transaction.addToBackStack(null)

                    fragment = it.fragment(tag)

                    if (fragment == null) {
                        showToast { "Section not available" }
                        return
                    }

                    transaction.add(R.id.main_fragment_content, fragment!!, tag)
                    transaction.commit()
                }
            }

            childFragmentManager.addOnBackStackChangedListener(onBackStackListener)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SECTION, section)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getParcelable<NavigationSection>(SECTION)?.let {
            section = it
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        childFragmentManager.fragments.last().onHiddenChanged(hidden)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        childFragmentManager.removeOnBackStackChangedListener(onBackStackListener)
    }

    fun launchFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            addToBackStack(null)
            hide(childFragmentManager.fragments.last())
            add(R.id.main_fragment_content, fragment)
            commit()
        }
    }

    fun onReselected() {
        val active = getChildActiveFragment() as? ScrollableToTopContent
        active?.goToUp()
    }

    fun actionClicked(actionId: Int) {
        val active = getChildActiveFragment() as? ActionsTopMenuListener
        active?.onActionClicked(actionId)
    }

}