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
package com.artear.rigel.extensions

import androidx.fragment.app.Fragment

/**
 * @return The last fragment on fragments list of child fragment manager.
 */
fun Fragment.getChildActiveFragment(): Fragment {
    return childFragmentManager.fragments.last()
}

/**
 * Useful to get a unique id an launch a fragment vertically.
 *
 * @return An id with child count +1.
 */
fun Fragment.getIdWithChildFragmentCount(description: String): String {
    return "${description}_${childFragmentManager.backStackEntryCount + 1}"
}