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
import com.artear.ui.base.ArtearActionBarProperties
import com.artear.ui.base.ArtearFragment
import com.artear.ui.interfaces.ArtearActionBarOwner

/**
 *  A Fragment that save an abstraction called *ArtearActionBarProperties* of some features to
 *  draw in a toolbar on [NavigationActivity].
 *
 *  See: uiview library
 *
 */
open class ActionBarFragment : ArtearFragment() {

    companion object {
        const val ACTION_BAR_PROPERTIES = "action_bar_properties"
    }

    /**
     * The abstraction to save toolbar properties of this fragment
     */
    lateinit var actionBarProperties: ArtearActionBarProperties

    /**
     * Called when a fragment recover the focus on screen. In that way the user can see an
     * update of toolbar.
     */
    fun updateActionBar() {
        val owner = context as? ArtearActionBarOwner
        owner?.updateActionBar(actionBarProperties, id)
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


