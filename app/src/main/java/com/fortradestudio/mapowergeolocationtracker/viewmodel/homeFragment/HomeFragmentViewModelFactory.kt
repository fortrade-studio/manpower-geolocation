package com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment

import android.app.Activity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeFragmentViewModelFactory(
    val view:View,
    val activity: Activity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeFragmentViewModel(view,activity) as T
    }

}