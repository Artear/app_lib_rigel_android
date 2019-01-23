package com.artear.rigel

import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment


interface NavigationSection : Parcelable {

    val titleSection: String

    val endpoint: String

    val idMenu: Int

    val position: Int

    fun fragment(fragmentId: String): Fragment

    override fun describeContents(): Int

    override fun writeToParcel(parcel: Parcel, flags: Int)
}