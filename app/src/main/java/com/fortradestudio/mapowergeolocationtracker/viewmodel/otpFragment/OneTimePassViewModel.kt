package com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.fortradestudio.mapowergeolocationtracker.repository.login.LoginRepository
import com.fortradestudio.mapowergeolocationtracker.repository.login.LoginRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OneTimePassViewModel(
    activity: Activity,
    view : View
) : ViewModel() {

    companion object{
        private const val TAG = "OneTimePassViewModel"
    }

    private val loginRepository : LoginRepository = LoginRepositoryImpl(activity)
    private val mainScope = CoroutineScope(Dispatchers.Main)

    fun verifyOtp(otp:String) = CoroutineScope(Dispatchers.IO).launch {
        loginRepository.verifyOTP(otp){
            if (it==-11){
                Log.i(TAG, "verifyOtp: error ")
            }
            else{
                Log.i(TAG, "verifyOtp: success")
            }
        }
    }

}
