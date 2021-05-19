package com.fortradestudio.mapowergeolocationtracker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.replace
import com.fortradestudio.mapowergeolocationtracker.ui.LoginFragment
import com.fortradestudio.mapowergeolocationtracker.ui.OneTimPasswordFragment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestLocationPermissionElseQuit()

    }

    private fun requestLocationPermissionElseQuit(){
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList, getString(R.string.dialog_reason), getString(
                        R.string.okay
                    ), getString(R.string.cancel)
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList, getString(R.string.need_to_allow), getString(
                        R.string.okay
                    ), getString(R.string.cancel)
                )
            }
            .request { allGranted, grantedList, deniedList ->
                 if(!allGranted) {
                    requestLocationPermissionElseQuit();
                }
                else{
                    // we need to open the setting to ask user to open the location
                     if(!checkifLocationOn()){
                         openLocationSetting();
                     }
                 }
            }
    }

    private fun openLocationSetting(){
        Toast.makeText(this, R.string.open_location, Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun checkifLocationOn():Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}