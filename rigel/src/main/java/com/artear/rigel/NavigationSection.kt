package com.artear.rigel

import android.os.Parcel
import android.os.Parcelable


interface NavigationSection : Parcelable {

    val titleSection: String

    val endpoint: String

    val idMenu: Int

    val position: Int

    override fun describeContents(): Int

    override fun writeToParcel(parcel: Parcel, flags: Int)
}