package com.fortradestudio.mapowergeolocationtracker.repository.login

interface LoginRepository {

    fun sendNumberForVerification(phoneNumber:String ,verificationResult:(Int)->Unit);
    fun verifyOTP(otp:String , onOTPVerified:(Int)->Unit)

}