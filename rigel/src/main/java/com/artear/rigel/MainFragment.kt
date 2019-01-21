package com.artear.rigel

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.artear.multitracker.MultiTracker
import com.artear.rigel.extensions.getChildActiveFragment
import com.artear.rigel.extensions.ifNull
import com.artear.ui.base.ArtearFragment


abstract class MainFragment : Fragment(), ArtearOnClickListener {

    private var fragment: ArtearFragment? = null

    companion object {
        private const val SECTION = "section"
        private const val CONTENT_FRAGMENT_TAG = "content_f_%s"

        private const val ARTICLE_FRAGMENT = "article_f_%s_%s"
        private const val CATEGORY_FRAGMENT = "category_f_%s_%s"
        private const val TAG_FRAGMENT = "tag_f_%s_%s"

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
        warn { "Flow - onBackStackListener - count = ${childFragmentManager.backStackEntryCount}" }
        if (childFragmentManager.backStackEntryCount > 0) {
            val artearFragment = childFragmentManager.fragments.last() as ActionBarFragment
            artearFragment.updateActionBar()
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

            val tag = CONTENT_FRAGMENT_TAG + section

            savedInstanceState?.let {
                fragment = childFragmentManager.findFragmentByTag(tag) as ContentFragment
            }.ifNull {
                childFragmentManager.beginTransaction().let { transaction ->
                    transaction.addToBackStack(null)

                    fragment = when (section) {
                        NavigationSection.COVER -> CoverFragment.newInstance(tag)
                        NavigationSection.RECIPES -> RecipesFragment.newInstance(tag)
                        NavigationSection.CUCINARE_TV -> CucinareTVFragment.newInstance(tag)
                        NavigationSection.NEWS -> NewsFragment.newInstance(tag)
                        else -> null
                    }

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
        warn { "MainFragment $section - onHiddenChanged - hidden: $hidden" }
        childFragmentManager.fragments.last().onHiddenChanged(hidden)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        childFragmentManager.removeOnBackStackChangedListener(onBackStackListener)
    }

    fun goToTop() {
        fragment?.goToUpContent()
        val active = getChildActiveFragment()
        when (active) {
            is ArticleFragment -> active.goToUpContent()
            is ContentFragment -> active.goToUpContent()
        }

    }

    private fun getIdFragment(fragmentId: String) =
            String.format(fragmentId, section!!.ordinal, childFragmentManager.backStackEntryCount + 1)


    override fun onArticleClick(link: Link) {
        val articleParameters = DeepLinkParser.parseArticle(link.internal, getString(R.string.codeBase))
        articleParameters?.let {
            MultiTracker.instance.send(SelectContentEvent(it.section, it.id.toString(),
                    it.title, it.category, it.section, ARTICLE))
        }
        launchFragment(ArticleFragment.newInstance(getIdFragment(ARTICLE_FRAGMENT), link))
    }

    override fun onCategoryClick(link: Link) {
        launchFragment(CategoryFragment.newInstance(getIdFragment(CATEGORY_FRAGMENT), link))
    }

    override fun onTagClick(link: Link) {
        val tagParameters = DeepLinkParser.parseTag(link.internal, getString(R.string.codeBase))
        tagParameters?.let {
            MultiTracker.instance.send(SelectContentEvent(TAG, it.id.toString(), it.title,
                    TAG, null, TAG))
        }
        launchFragment(TagsFragment.newInstance(getIdFragment(TAG_FRAGMENT), link))
    }

    private fun launchFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().let { transaction ->
            transaction.addToBackStack(null)
            transaction.hide(childFragmentManager.fragments.last())
            transaction.add(R.id.main_fragment_content, fragment)
            transaction.commit()
        }
    }

}