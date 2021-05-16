package com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.repository.login.LoginRepository
import com.fortradestudio.mapowergeolocationtracker.repository.login.LoginRepositoryImpl
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragmentViewModel(
    activity: Activity,
    val view:View
) : ViewModel() {

    companion object {
        private const val TAG = "LoginFragmentViewModel"
    }

    private val loginRepository: LoginRepository = LoginRepositoryImpl(activity)

    // only checking for the length of the number for now
    fun checkIfMobileNumberIsValid(phoneNumber: String, onVerified: (Boolean) -> Unit) {
        onVerified(phoneNumber.length == 10)
    }

    fun sendNumberForOTP(phoneNumber: String) = CoroutineScope(Dispatchers.IO).launch {
        loginRepository.sendNumberForVerification(phoneNumber) {
            when (it) {
                0 -> {
                    // this is where we navigate to enter otp fragment
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_oneTimPasswordFragment)
                }
                1 -> {
                    Log.i(TAG, "sendNumberForOTP: $it")
                }
                2 -> {
                    Log.i(TAG, "sendNumberForOTP: $it")
                }
            }
        }
    }


}