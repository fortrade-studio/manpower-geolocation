package com.fortradestudio.mapowergeolocationtracker.repository.login

import android.app.Activity
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginRepositoryImpl(private val activity: Activity) : LoginRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        const val COMPLETION_CODE = 1;
        const val ERROR_CODE = -1;
        const val VERIFICATION_SENT_CODE = 0;

        const val ON_OTP_INVALID = -11;
        const val ON_OTP_VALID = 10;

        const val verificationIdKey = "verification"
    }

    // here we will be sending the verification for the number
    override fun sendNumberForVerification(phoneNumber: String, verificationResult: (Int) -> Unit) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    verificationResult(COMPLETION_CODE)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    verificationResult(ERROR_CODE)
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    // p0 -> verification ID , p1 -> token
                    // they both need to be saved so we will store them in cache using utils class
                    Utils(activity).storeInCache(p0, verificationIdKey)
                    verificationResult(VERIFICATION_SENT_CODE)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyOTP(otp: String, onOTPVerified: (Int) -> Unit) {
        val fromCache = Utils(activity).getFromCache(verificationIdKey)
        if (fromCache != null) {
            val credential = PhoneAuthProvider.getCredential(fromCache, otp)
            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        onOTPVerified(ON_OTP_VALID)
                    } else {
                        // Sign in failed, display a message and update the UI
                        if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            onOTPVerified(ON_OTP_INVALID);
                        }
                        // Update UI
                    }
                }
        }
    }


}