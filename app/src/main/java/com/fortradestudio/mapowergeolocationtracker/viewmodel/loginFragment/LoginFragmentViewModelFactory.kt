package com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginFragmentViewModelFactory(
    val activity: Activity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginFragmentViewModel(activity = activity) as T
    }

}