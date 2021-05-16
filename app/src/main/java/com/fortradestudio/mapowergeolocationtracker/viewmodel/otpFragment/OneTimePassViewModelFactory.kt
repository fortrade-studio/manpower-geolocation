package com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment

import android.app.Activity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OneTimePassViewModelFactory (
    val activity: Activity,
    val view : View
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OneTimePassViewModel(activity,view) as T
    }
}