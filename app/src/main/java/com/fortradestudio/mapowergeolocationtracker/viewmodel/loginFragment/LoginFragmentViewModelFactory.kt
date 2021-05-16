package com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment

import android.app.Activity
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginFragmentViewModelFactory(
    val activity: Activity,
    val view :View
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginFragmentViewModel(activity = activity,view = view) as T
    }

}