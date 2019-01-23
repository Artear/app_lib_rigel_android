package com.artear.rigelexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artear.rigel.ActionBarFragment
import com.artear.rigel.MainFragment
import com.artear.rigel.extensions.getIdWithChildFragmentCount
import com.artear.ui.base.ArtearActionBarProperties
import kotlinx.android.synthetic.main.example_fragment.*


class ExampleFragment : ActionBarFragment() {

    companion object {
        const val ARG_COUNT = "count"
        const val POSITION = "position"
        const val ENDPOINT = "endpoint"

        const val DESCRIPTION_FRAGMENT = "example"
    }

    private var count: Int = 0
    private var position: Int = 0
    private var endpoint: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            count = it.getInt(ARG_COUNT, 0)
            position = it.getInt(POSITION)
            endpoint = it.getString(ENDPOINT)
            id = it.getString(FRAGMENT_ID)!!
            title = it.getString(ARTEAR_FRAGMENT_TITLE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.example_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionBarProperties = ArtearActionBarProperties(title)

        val title = "${fragmentTitle.text} $count"
        fragmentTitle.text = title

        buttonAnother.setOnClickListener {
            val launcher = parentFragment as? MainFragment
            launcher?.run {
                //Here i am the parent Fragment
                launchFragment(ExampleSection.values()[position].fragment(
                        getIdWithChildFragmentCount(DESCRIPTION_FRAGMENT))
                )
            }
        }

        buttonSelf.setOnClickListener {
            val launcher = parentFragment as? MainFragment
            launcher?.run {
                val fragment = ExampleSection.values()[position].fragment("")
                fragment.arguments?.putInt(ARG_COUNT, count.plus(1))
                launchFragment(fragment)
            }
        }
    }


}
