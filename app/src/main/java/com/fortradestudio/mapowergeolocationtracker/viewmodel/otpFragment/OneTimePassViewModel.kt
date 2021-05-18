package com.fortradestudio.mapowergeolocationtracker.viewmodel.otpFragment

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.repository.login.LoginRepository
import com.fortradestudio.mapowergeolocationtracker.repository.login.LoginRepositoryImpl
import com.fortradestudio.mapowergeolocationtracker.ui.LoginFragment
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.fortradestudio.mapowergeolocationtracker.viewmodel.loginFragment.LoginFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OneTimePassViewModel(
    val activity: Activity,
    val view: View
) : ViewModel() {

    companion object {
        private const val TAG = "OneTimePassViewModel"
        private const val number_cache_key = "phoneNumber"

    }


    private lateinit var dialog: androidx.appcompat.app.AlertDialog
    private val loginRepository: LoginRepository = LoginRepositoryImpl(activity)
    private val mainScope = CoroutineScope(Dispatchers.Main)

    fun verifyOtp(otp: String, onResult: (Boolean) -> Unit) =
        CoroutineScope(Dispatchers.IO).launch {
            loginRepository.verifyOTP(otp) {
                dialog.cancel()
                if (it == -11) {
                    onResult(false)
                } else {
                    onResult(true)
                    Navigation.findNavController(view)
                        .navigate(R.id.action_oneTimPasswordFragment_to_homeFragment)
                }
            }
        }
    fun checkIfMobileNumberIsValid(phoneNumber: String, onVerified: (Boolean) -> Unit) {
        onVerified (validator(phoneNumber))
    }

    private fun validator(ph:String):Boolean{
        return ph.matches(Regex("[0-9]+")) && ph.length == 10
    }

    fun sendNumberForOTP(phoneNumber: String) = CoroutineScope(Dispatchers.IO).launch {
        loginRepository.sendNumberForVerification(phoneNumber) {
            when (it) {
                0 -> {
                    // this is where we navigate to enter otp fragment
                    Utils(activity).storeInCache(
                        phoneNumber,
                        OneTimePassViewModel.number_cache_key
                    )
                    dialog.cancel()
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_oneTimPasswordFragment)
                }
                1 -> {
                    Log.i(OneTimePassViewModel.TAG, "sendNumberForOTP: $it")
                }
                2 -> {
                    Log.i(OneTimePassViewModel.TAG, "sendNumberForOTP: $it")
                }
            }
        }
    }

    fun showDialog() {
        val inflator = LayoutInflater.from(activity).inflate(R.layout.loading_dialog, null)
        val view = inflator.rootView

        val loadingAnimatedView = view.findViewById<ImageView>(R.id.loadingIcon)
        val drawable = loadingAnimatedView.drawable

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (drawable is AnimatedVectorDrawable) {
                val animation = drawable as AnimatedVectorDrawable?
                animation?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        animation.start()
                    }
                })
                animation?.start()
            } else if (drawable is AnimatedVectorDrawableCompat) {
                val animation = drawable as AnimatedVectorDrawableCompat
                animation.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        animation.start()
                    }
                })
                animation.start()
            }
        } else {
            if (drawable is AnimatedVectorDrawableCompat) {
                val animation = drawable as AnimatedVectorDrawableCompat
                animation.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        animation.start()
                    }
                })
                animation.start()
            }
        }


        dialog = MaterialAlertDialogBuilder(activity)
            .setView(view)
            .setCancelable(false)
            .show()

    }





}
