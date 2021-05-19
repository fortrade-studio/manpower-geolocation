package com.fortradestudio.mapowergeolocationtracker.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.AsyncTask
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.fortradestudio.mapowergeolocationtracker.MainActivity
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationDao
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddressDatabase
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddressRepository
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddresses
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import kotlinx.coroutines.*
import java.util.logging.Handler

@SuppressLint("NewApi")
class NotificationService : JobService() {

    companion object {

        private const val TAG = "NotificationService"

        const val CLOCK_IN_CACHE_KEY = "clockin"
        const val CHANNEL_ID = "fortrade"
        const val title = "Hello This is arya"
        const val contextText = "Checking if the notification is wokring btw"
        const val notification_id = 1001;
        const val notification_Cache = "notificationcache"
    }

    val ioScope = CoroutineScope(Dispatchers.IO)
    val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onStartJob(params: JobParameters?): Boolean {
        val dao = VendorAddressDatabase.getDatabase(context = this).getDao()

        Log.i(TAG, "doWork: work is here");
        // first we need to check if user have clocked in because then notification is not required

        mainScope.launch {
            val fromCache =
                Utils(Activity()).getFromCache(CLOCK_IN_CACHE_KEY, this@NotificationService)
            if (fromCache == true) {
                return@launch
            }
            LocationUtils(
                Activity(),
                this@NotificationService
            ).getLocationCoordinates(this@NotificationService, { l ->
                ioScope.launch {
                    val repository = VendorAddressRepository(dao)
                    val filter = repository.getAllAddressesSync()
                        .filter { filterThoseInRange(it, l) }

                    Log.i(TAG, "onStartJob: ${filter.toString()} ")

                    if (filter.isNotEmpty()) {
                        //  result found so notification
                        withContext(Dispatchers.Main) {
                            Log.i(TAG, "onStartJob: reaching notification sender")
                            sendNotification()
                        }
                    }

                }
            }) {}
        }

        return true;
    }

    private fun sendNotification() {
        val pendingIntent= NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.homeFragment)
            .createPendingIntent()

        val notification: Notification = NotificationCompat.Builder(this,
            NotificationService.CHANNEL_ID
        )
            .setContentTitle(NotificationService.title)
            .setContentText(NotificationService.contextText)
            .setPriority(2)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        notification.flags = Notification.FLAG_AUTO_CANCEL


        val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NotificationService.notification_id,notification)


    }



    private fun filterThoseInRange(
        vendorAddresses: VendorAddresses,
        myLocation: Location
    ): Boolean {
        val calculateLinearDistance = LocationUtils.calculateLinearDistance(
            myLocation,
            LocationDao(vendorAddresses.latitude, vendorAddresses.longitude)
        )
        Log.i(TAG, "filterThoseInRange: $calculateLinearDistance")
        return calculateLinearDistance <= 500
    }


    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i(TAG, "onStopJob: ")
        return true;
    }
}