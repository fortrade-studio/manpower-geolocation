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
import com.google.android.gms.location.*
import java.lang.Math.sin
import java.lang.NullPointerException


class LocationUtils(
    val activity: Activity,
    val context: Context?=null
) {

    val locationServices: FusedLocationProviderClient by lazy {
       getLocationProvider()
    }

    private fun getLocationProvider() : FusedLocationProviderClient= if(context==null) {
        LocationServices.getFusedLocationProviderClient(activity)
    }
    else
    {
        LocationServices.getFusedLocationProviderClient(context)
    }


    @SuppressLint("MissingPermission")
    fun getLocationCoordinates(onLocationFetched: (Location) -> Unit, onError: (Exception) -> Unit){

        if(checkifLocationOn()){
            locationServices.lastLocation.addOnSuccessListener {
                if(it==null ){
                    if(context==null) {
                        requestLocationAgain(LocationCallbackExtension({
                            onLocationFetched(it)
                        }) { onError(NullPointerException()) });
                    }
                }else{
                    onLocationFetched(it)
                }
            }.addOnFailureListener {
                onError(it)
            }
        }
        else{
            if(context==null) openLocationSetting()
        }

    }    @SuppressLint("MissingPermission")
    fun getLocationCoordinates(context: Context,onLocationFetched: (Location) -> Unit, onError: (Exception) -> Unit){

        if(checkifLocationOn(context)){
            locationServices.lastLocation.addOnSuccessListener {
                if(it!=null ){
                    onLocationFetched(it)
                }
            }.addOnFailureListener {
                onError(it)
            }
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


    companion object{
         fun calculateLinearDistance(location: Location, target: LocationDao): Double {
            val R = 6378.137; // Radius of earth in KM
            val dLat = target.latitude * Math.PI / 180 - location.latitude * Math.PI / 180;
            val dLon = target.longitude * Math.PI / 180 - location.longitude * Math.PI / 180;
            val a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(location.latitude * Math.PI / 180) * Math.cos(target.latitude * Math.PI / 180) *
                    Math.sin(dLon/2) * sin(dLon/2);
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            val d = R * c;

            return d * 1000
        }
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
    private fun checkifLocationOn(context: Context):Boolean{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        );
    }


}