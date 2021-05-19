package com.fortradestudio.mapowergeolocationtracker.locationsUtils

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class LocationCallbackExtension(
    val onLocationResultCallback:(Location)->Unit,
    val onErrorCallback:()->Unit
) : LocationCallback() {

    override fun onLocationResult(p0: LocationResult?) {
        if(p0!=null) {
            val location = p0.lastLocation
            onLocationResultCallback(location)
        }else{
            onErrorCallback();
        }
    }

}