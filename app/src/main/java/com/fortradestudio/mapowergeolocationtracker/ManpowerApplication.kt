package com.fortradestudio.mapowergeolocationtracker

import android.app.Application
import androidx.work.Configuration

class ManpowerApplication : Application(), Configuration.Provider {

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.ERROR)
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

}