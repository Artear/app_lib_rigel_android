package com.artear.rigel.extensions

import android.support.v4.app.Fragment


/**
 * @return The last fragment on fragments list of child fragment manager.
 */
fun Fragment.getChildActiveFragment(): Fragment {
    return childFragmentManager.fragments.last()
}