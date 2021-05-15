package com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.fortradestudio.mapowergeolocationtracker.repository.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragmentViewModel(
    activity: Activity
) : ViewModel(){

    private lateinit var loginRepository: LoginRepository

    // only checking for the length of the number for now
    fun checkIfMobileNumberIsValid(phoneNumber:String,onVerified:(Boolean)->Unit){
         onVerified(phoneNumber.length==10)
    }

    fun sendNumberForOTP()= CoroutineScope(Dispatchers.IO).launch {
        loginRepository.sendNumberForVerification {
            when(it){
                 0 ->{}
                 1 ->{}
                 2 ->{}
            }
        }
    }


}