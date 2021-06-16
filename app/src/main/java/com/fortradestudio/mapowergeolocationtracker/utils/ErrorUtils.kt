package com.fortradestudio.mapowergeolocationtracker.utils

import android.util.Log
import com.fortradestudio.mapowergeolocationtracker.Time
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ErrorUtils {

    /*
    java.lang.ExceptionInInitializerError
    at okhttp3.internal.platform.Platform.get(Platform.java:85)
    at okhttp3.OkHttpClient.newSslSocketFactory(OkHttpClient.java:263)
    at okhttp3.OkHttpClient.<init>(OkHttpClient.java:229)
    at okhttp3.OkHttpClient.<init>(OkHttpClient.java:202)
    at retrofit2.Retrofit$Builder.build(Retrofit.java:614)
    at com.fortradestudio.mapowergeolocationtracker.retrofit.RetrofitProvider.<clinit>(RetrofitProvider.kt:17)
    	at com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment.HomeFragmentViewModel.getLabourName(HomeFragmentViewModel.kt:45)
    	at com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment.onViewCreated(HomeFragment.kt:79)
    	at androidx.fragment.app.FragmentStateManager.createView(FragmentStateManager.java:332)
    	at androidx.fragment.app.FragmentManager.moveToState(FragmentManager.java:1187)
    	at androidx.fragment.app.FragmentManager.addAddedFragments(FragmentManager.java:2224)
    		at androidx.fragment.app.FragmentManager.executeOpsTogether(FragmentManager.java:1997)
    			at androidx.fragment.app.FragmentManager.removeRedundantOperationsAndExecute(FragmentManager.java:1953)
    			at androidx.fragment.app.FragmentManager.execPendingActions(FragmentManager.java:1849)
    			at androidx.fragment.app.FragmentManager$4.run(FragmentManager.java:413)
    				at android.os.Handler.handleCallback(Handler.java:938)
    				at android.os.Handler.dispatchMessage(Handler.java:99)
    				at android.os.Looper.loop(Looper.java:239)
    				at android.app.ActivityThread.main(ActivityThread.java:8205)
    					at java.lang.reflect.Method.invoke(Native Method)
    					at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:626)
    						at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1015)Caused by: java.lang.IllegalStateException: Expected Android API level 21+ but was 30	at okhttp3.internal.platform.AndroidPlatform.buildIfSupported(AndroidPlatform.java:238)
    						at okhttp3.internal.platform.Platform.findPlatform(Platform.java:202)
    	at okhttp3.internal.platform.Platform.<clinit>(Platform.java:79)	... 22 more
     */


    /*

    java.lang.NullPointerException: null cannot be cast to non-null type kotlin.CharSequence
    at com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment.HomeFragmentViewModel$getLabourName$1.onResponse(HomeFragmentViewModel.kt:55)
    at retrofit2.DefaultCallAdapterFactory$ExecutorCallbackCall$1.lambda$onResponse$0$DefaultCallAdapterFactory$ExecutorCallbackCall$1(DefaultCallAdapterFactory.java:89)
    at retrofit2.-$$Lambda$DefaultCallAdapterFactory$ExecutorCallbackCall$1$hVGjmafRi6VitDIrPNdoFizVAdk.run(Unknown Source:6)	at android.os.Handler.handleCallback(Handler.java:938)
    at android.os.Handler.dispatchMessage(Handler.java:99)
    at android.os.Looper.loop(Looper.java:239)
    	at android.app.ActivityThread.main(ActivityThread.java:8205)
    	at java.lang.reflect.Method.invoke(Native Method)
    	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:626)
    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1015)
     */

    companion object{
        private const val TAG = "ErrorUtils"
    }

    private val firestore = FirebaseFirestore.getInstance()

    fun report(e: Throwable){
        firestore.collection("Error")
            .document(System.currentTimeMillis().toString())
            .set(ErrorData(e.stackTraceToString(),calculateCurrentDate(),Time().calculateTime()))
    }

    fun reportForId(e: Throwable):String{
        val docId = System.currentTimeMillis().toString()
        firestore.collection("Error")
            .document(docId)
            .set(ErrorData(e.stackTraceToString(),calculateCurrentDate(),Time().calculateTime()))

        return docId
    }

    private fun calculateCurrentDate(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    data class ErrorData(
        val error:String,
        val date:String,
        val time:String
    )

}