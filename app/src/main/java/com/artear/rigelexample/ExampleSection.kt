package com.artear.rigelexample

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.artear.rigel.NavigationSection
import com.artear.rigel.getUniqueId
import com.artear.rigelexample.ExampleFragment.Companion.ENDPOINT
import com.artear.rigelexample.ExampleFragment.Companion.POSITION
import com.artear.ui.base.ArtearFragment.Companion.FRAGMENT_ID
import com.artear.ui.base.ArtearFragment.Companion.FRAGMENT_TITLE
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


enum class ExampleSection(override val titleSection: String,
                          override val endpoint: String,
                          override val idMenu: Int,
                          private val fragmentClass: KClass<out Fragment>) : NavigationSection {

    FIRST("First", "first", R.id.menu_first, ExampleFragment::class),
    SECOND("Second", "second", R.id.menu_second, ExampleFragment::class),
    THIRD("Third", "third", R.id.menu_third, ExampleFragment::class),
    FOURTH("Fourth", "fourth", R.id.menu_fourth, ExampleFragment::class);

    override val position: Int
        get() = ordinal

    override fun fragment(fragmentId: String): Fragment {
        return fragmentClass.createInstance().apply {
            arguments = Bundle().apply {
                putInt(POSITION, this@ExampleSection.ordinal)
                putString(ENDPOINT, this@ExampleSection.endpoint)
                putString(FRAGMENT_TITLE, this@ExampleSection.titleSection)
                putString(FRAGMENT_ID, getUniqueId(this@ExampleSection, fragmentId))
            }
        }
    }

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