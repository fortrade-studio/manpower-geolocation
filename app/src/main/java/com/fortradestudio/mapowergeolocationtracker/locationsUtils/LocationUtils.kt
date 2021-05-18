package com.fortradestudio.mapowergeolocationtracker.locationsUtils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import com.fortradestudio.mapowergeolocationtracker.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.lang.NullPointerException


class LocationUtils(
    val activity: Activity
) {

    val locationServices by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }



    @SuppressLint("MissingPermission")
    fun getLocationCoordinates(onLocationFetched: (Location) -> Unit, onError: (Exception) -> Unit){


        if(checkifLocationOn()){
            locationServices.lastLocation.addOnSuccessListener {
                if(it==null){
                    requestLocationAgain(LocationCallbackExtension({
                        onLocationFetched(it)
                    }){onError(NullPointerException())});
                }else{
                    onLocationFetched(it)
                }
            }.addOnFailureListener {
                onError(it)
            }
        }
        else{
           openLocationSetting()
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestLocationAgain(locationCallbackExtension: LocationCallbackExtension){
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        locationServices.requestLocationUpdates(mLocationRequest, locationCallbackExtension,Looper.myLooper())
    }



    private fun openLocationSetting(){
        Toast.makeText(activity, R.string.open_location, Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }

    private fun checkifLocationOn():Boolean{
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        );
    }

}