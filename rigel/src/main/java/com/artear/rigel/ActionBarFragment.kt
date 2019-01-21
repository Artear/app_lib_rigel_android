package com.artear.rigel

import android.os.Bundle
import com.artear.ui.base.ArtearActionBarProperties
import com.artear.ui.base.ArtearFragment
import com.artear.ui.interfaces.ArtearActionBarOwner


open class ActionBarFragment : ArtearFragment() {

    companion object {
        const val ACTION_BAR_PROPERTIES = "action_bar_properties"
    }

    lateinit var actionBarProperties: ArtearActionBarProperties

    fun updateActionBar() {
        context?.let {
            if (it is ArtearActionBarOwner) {
                it.updateActionBar(actionBarProperties, id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreActionBarProperties(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ACTION_BAR_PROPERTIES, actionBarProperties)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoreActionBarProperties(savedInstanceState)
    }

    private fun restoreActionBarProperties(savedInstanceState: Bundle?) {
        savedInstanceState?.apply {
            getParcelable<ArtearActionBarProperties>(ACTION_BAR_PROPERTIES)?.let { props ->
                actionBarProperties = props
            }
        }
    }
}


