package com.artear.rigelexample

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artear.rigel.MainFragment
import kotlinx.android.synthetic.main.example_fragment.*


class ExampleFragment : Fragment() {

    companion object {
        const val ARG_COUNT = "count"
        const val POSITION = "position"
        const val TITLE = "title"
        const val ENDPOINT = "endpoint"
    }

    private var count: Int = 0
    private var position: Int = 0
    private var title: String? = null
    private var endpoint: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            count = it.getInt(ARG_COUNT, 0)
            position = it.getInt(POSITION)
            title = it.getString(TITLE)
            endpoint = it.getString(ENDPOINT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.example_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = "${fragmentTitle.text} $count"
        fragmentTitle.text = title

        buttonAnother.setOnClickListener {
            val launcher = parentFragment as? MainFragment
            launcher?.run { launchFragment(ExampleSection.values()[position].fragment()) }
        }

        buttonSelf.setOnClickListener {
            val launcher = parentFragment as? MainFragment
            launcher?.run {
                val fragment = ExampleSection.values()[position].fragment()
                fragment.arguments?.putInt(ARG_COUNT, count.plus(1))
                launchFragment(fragment)
            }
        }
    }

    private fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

}
