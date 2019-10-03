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

import android.os.Parcelable
import androidx.fragment.app.Fragment

/**
 * A section for navigation flow. Join a menu button with a section fragment. The position must be
 * the same position on that menu.
 *
 * The endpoint just is used if the fragment is connected with specific and unique section api.
 *
 * Used in [MainFragment] to instantiate a fragment and set a title.
 *
 */
interface NavigationSection : Parcelable {

    val titleSection: String

    val endpoint: String

    val idMenu: Int

    /**
     * The menu position in the same order
     */
    val position: Int

    /**
     * The fragment to manage this section
     */
    fun fragment(fragmentId: String): Fragment
}