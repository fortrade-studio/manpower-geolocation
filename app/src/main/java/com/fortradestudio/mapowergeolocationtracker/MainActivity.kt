package com.fortradestudio.mapowergeolocationtracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment
import com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment.ClockFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.permissionx.guolindev.PermissionX
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "fortrade"
        const val notification_extras = "notificationExtra"
        const val notification_Cache = "notificationcache"
        private const val TAG = "MainActivity"
        lateinit var  appUpdateManager:AppUpdateManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // we will also check for the update here also
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdate(appUpdateManager)

        requestLocationPermissionElseQuit()
        createNotificationChannel()

        val preferences =
            this.applicationContext.getSharedPreferences("notification", Context.MODE_PRIVATE)
        val editor = preferences.getString(HomeFragment.notification_Cache, null)
        Log.i(TAG, "onCreate: $editor")
    }

    private fun checkUpdate(appUpdateManager: AppUpdateManager) {
        val appUpdateInfo = appUpdateManager.appUpdateInfo
        appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // this is where we will show the alert box
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.material_title)
                    .setMessage(R.string.material_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.material_update) { dialogInterface: DialogInterface, i: Int ->
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$packageName")
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                )
                            )
                        }
                    }.setNegativeButton(R.string.material_exit) { d, i ->
                        finish()
                    }.show()
            }
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

        }
    }

    private fun requestLocationPermissionElseQuit() {
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
                if (!allGranted) {
                    requestLocationPermissionElseQuit();
                } else {
                    // we need to open the setting to ask user to open the location
                    if (!checkifLocationOn()) {
                        openLocationSetting();
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        checkUpdate(appUpdateManager)
    }

    private fun openLocationSetting() {
        Toast.makeText(this, R.string.open_location, Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun checkifLocationOn(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        );
    }
}