package com.fortradestudio.mapowergeolocationtracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private var phoneNumber = MutableLiveData("1234567890")
    val phNumber: LiveData<String> = phoneNumber

    fun savePhoneNumber(newPhoneNumber: String){
        phoneNumber.value = newPhoneNumber
    }
}