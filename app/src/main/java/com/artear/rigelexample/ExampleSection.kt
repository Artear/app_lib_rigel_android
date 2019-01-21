package com.artear.rigelexample

import android.os.Parcel
import android.os.Parcelable
import com.artear.rigel.NavigationSection


enum class ExampleSection(override val titleSection: String,
                          override val endpoint: String,
                          override val idMenu: Int) : NavigationSection {

    FIRST("First", "first", R.id.menu_first),
    SECOND("Second", "second", R.id.menu_second),
    THIRD("Third", "third", R.id.menu_third),
    FOURTH("Fourth", "fourth", R.id.menu_fourth);

    override val position: Int
        get() = ordinal

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExampleSection> {
        override fun createFromParcel(parcel: Parcel): ExampleSection {
            return ExampleSection.values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<ExampleSection?> {
            return arrayOfNulls(size)
        }
    }

}