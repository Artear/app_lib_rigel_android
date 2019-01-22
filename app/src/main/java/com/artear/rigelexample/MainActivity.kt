package com.artear.rigelexample

import android.support.design.widget.BottomNavigationView
import android.support.v7.content.res.AppCompatResources
import com.artear.rigel.NavigationActivity
import com.artear.rigel.NavigationProvider

class MainActivity : NavigationActivity() {

    override var navigationProvider: NavigationProvider =
            NavigationProvider(ExampleSection.values(), R.id.menu_first)

    override fun onBottomNavigationViewCreated(bottomNavigationView: BottomNavigationView){
        bottomNavigationView.apply {
            inflateMenu(R.menu.menu_bottom_navigation)
            itemIconTintList = AppCompatResources.getColorStateList(context, R.color.navigation_item_icon)
            itemTextColor = AppCompatResources.getColorStateList(context, R.color.navigation_item_icon)
        }
    }
}