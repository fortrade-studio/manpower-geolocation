package com.fortradestudio.mapowergeolocationtracker.repository.login

interface LoginRepository {

    fun sendNumberForVerification(verificationResult:(Int)->Unit);

}