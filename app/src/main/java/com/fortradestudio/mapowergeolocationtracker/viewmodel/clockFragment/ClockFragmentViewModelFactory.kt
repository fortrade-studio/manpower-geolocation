package com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment

import android.app.Activity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClockFragmentViewModelFactory(
    val activity: Activity,
    val view:View
):ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClockFragmentViewModel(activity,view) as T
    }


}