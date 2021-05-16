package com.fortradestudio.mapowergeolocationtracker.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.prefs.PreferenceChangeEvent

class Utils(private val activity: Activity) {

    companion object{
        private const val verifyPreference ="verification_preferences"
    }

    // to store in cache
    fun storeInCache(s:String,key:String){
        val preferences = activity.getSharedPreferences(verifyPreference,Context.MODE_PRIVATE)
        val editor = preferences.edit();
        editor.putString(key,s);
        editor.apply()
    }

    fun getFromCache(key: String):String?{
        val preferences = activity.getSharedPreferences(verifyPreference,Context.MODE_PRIVATE)
        return preferences.getString(key,null);
    }

}