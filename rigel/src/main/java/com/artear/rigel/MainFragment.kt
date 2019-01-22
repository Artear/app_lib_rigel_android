package com.artear.rigel

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.artear.rigel.extensions.getChildActiveFragment
import com.artear.rigel.extensions.ifNull


class MainFragment : Fragment() {


    companion object {
        private const val SECTION = "section"
        private const val CONTENT_FRAGMENT_TAG = "content_f_%s"


//        private const val ARTICLE_FRAGMENT = "article_f_%s_%s"
//        private const val CATEGORY_FRAGMENT = "category_f_%s_%s"
//        private const val TAG_FRAGMENT = "tag_f_%s_%s"

        fun newInstance(navigationSection: NavigationSection) =
                MainFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(SECTION, navigationSection)
                    }
                }
    }

    protected var section: NavigationSection? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    private val onBackStackListener = {
        //        warn { "Flow - onBackStackListener - count = ${childFragmentManager.backStackEntryCount}" }
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

            val tag = String.format(CONTENT_FRAGMENT_TAG, it.toString())
            var fragment: Fragment?

            savedInstanceState?.let {
                fragment = childFragmentManager.findFragmentByTag(tag)
            }.ifNull {
                childFragmentManager.beginTransaction().let { transaction ->
                    transaction.addToBackStack(null)

                    fragment = it.fragment()

                    if (fragment == null) {
                        Toast.makeText(context, "Section not available", Toast.LENGTH_LONG).show()
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
//        warn { "MainFragment $section - onHiddenChanged - hidden: $hidden" }
        childFragmentManager.fragments.last().onHiddenChanged(hidden)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        childFragmentManager.removeOnBackStackChangedListener(onBackStackListener)
    }

    private fun getIdFragment(fragmentId: String) =
            String.format(fragmentId, section!!.position, childFragmentManager.backStackEntryCount + 1)

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