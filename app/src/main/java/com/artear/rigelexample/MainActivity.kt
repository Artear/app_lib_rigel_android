package com.artear.rigelexample

import androidx.appcompat.content.res.AppCompatResources
import com.artear.rigel.MainFragment
import com.artear.rigel.NavigationProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.reflect.KClass

class MainActivity : BaseActivity() {

    override var navigationProvider: NavigationProvider =
            NavigationProvider(ExampleSection.values(), R.id.menu_first)

    override fun getMainFragmentClass(): KClass<out MainFragment> {
        return MainExampleFragment::class
    }

    override fun onBottomNavigationViewCreated(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.apply {
            inflateMenu(R.menu.menu_bottom_navigation)
            itemIconTintList = AppCompatResources.getColorStateList(context, R.color.navigation_item_icon)
            itemTextColor = AppCompatResources.getColorStateList(context, R.color.navigation_item_icon)
        }
    }
}