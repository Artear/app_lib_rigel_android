package com.artear.rigelexample

import android.os.Bundle
import com.artear.rigel.NavigationActivity


abstract class BaseActivity : NavigationActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}