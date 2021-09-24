package com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment

import android.app.Activity
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.Time
import com.fortradestudio.mapowergeolocationtracker.retrofit.LabourRecord
import com.fortradestudio.mapowergeolocationtracker.retrofit.LabourUploadRecord
import com.fortradestudio.mapowergeolocationtracker.retrofit.RetrofitProvider
import com.fortradestudio.mapowergeolocationtracker.room.User
import com.fortradestudio.mapowergeolocationtracker.utils.CacheUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ClockFragmentViewModel(
    private val activity: Activity,
    private val view: View
) : ViewModel() {

    companion object {
        private const val TAG = "ClockFragmentViewModel"
    }

    // state = 0 , 1 , 2 ,-1 -> 0 means loading , 1 means clock in , 2 means clock out , -1 means error
    val state = MutableLiveData<Int>(0)

    fun getMutableState(): MutableLiveData<Int> {
        return state
    }

    fun postState(value: Int) {
        state.postValue(value)
    }

    val ioScope = CoroutineScope(Dispatchers.IO)
    lateinit var dialog: AlertDialog
    var laboursList: ArrayList<LabourRecord> = ArrayList();

    // on upload(true)-> means data uploaded successfully
    // on upload(false)-> means data uploaded un_successfully
    fun uploadData(onUpload: (Boolean) -> Unit, onFailureError: (Throwable) -> Unit) {

        CacheUtils(activity).getUserData {
            val labourServiceRepository = RetrofitProvider.getLabourServiceRepository()

            if (laboursList.isNotEmpty()) {
                for (record in laboursList) {
                    if (record.Address.trim() != it.address.trim() && record.clockedOut.trim() != "y" && record.Time_out == "21:00:00") {
                        ioScope.launch {
                            // here we update the time out of all the others entries that weren't clocked out
                            clockOutData({}, calculateCurrentTime(), User(
                                name = record.Labor_Name,
                                vendorName = record.Vendor_Name,
                                phoneNumber = record.phNo,
                                projectId = record.PID,
                                category = record.Category,
                                address = record.Address
                            ), {})
                        }
                    }
                }
            }

            labourServiceRepository.uploadClockInTime(mapUserToLabourRecord(it))
                .enqueue(object : retrofit2.Callback<LabourRecord> {
                    override fun onResponse(
                        call: Call<LabourRecord>,
                        response: Response<LabourRecord>
                    ) {
                        if (response.isSuccessful) {
                            state.postValue(2)
                            onUpload(true)
                        } else {
                            onUpload(false)
                        }
                    }

                    override fun onFailure(call: Call<LabourRecord>, t: Throwable) {
                        onFailureError(t)
                    }
                })
        }
    }

    fun clockOutData(
        onSuccessFullClockedOut: () -> Unit,
        time: String = calculateCurrentTime(),
        user: User? = null,
        onFailure: () -> Unit
    ) {
        // before clock out we need to check if user previously clocked in
        CacheUtils(activity).getUserData {
            val labourServiceRepository = RetrofitProvider.getLabourServiceRepository()

            val defaultUpl = if (user == null) {
                generateUPLI(it).trim()
            } else {
                generateUPLI(user).trim()
            }

            labourServiceRepository.updateUserClockOut(
                defaultUpl,
                // when we update we also update the upli
                LabourUploadRecord(time, "y", "")
            ).enqueue(object : Callback<LabourRecord> {
                override fun onResponse(
                    call: Call<LabourRecord>,
                    response: Response<LabourRecord>
                ) {
                    if (response.isSuccessful) {
                        state.postValue(1)
                        onSuccessFullClockedOut()
                        Log.e(TAG, "onResponse: success");
                    } else {
                        onFailure()
                        Log.i(TAG, "onResponse: ${response.message()}")
                        Log.i(TAG, "onResponse: failed")
                    }
                }

                override fun onFailure(call: Call<LabourRecord>, t: Throwable) {
                    onFailure()
                    Log.e(TAG, "onFailure: failed", t)
                }

            })

        }
    }

    fun showDialog() {
        val inflator = LayoutInflater.from(activity).inflate(R.layout.loading_dialog, null)
        val view = inflator.rootView

        val loadingAnimatedView = view.findViewById<ImageView>(R.id.loadingIcon)
        val drawable = loadingAnimatedView.drawable

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (drawable is AnimatedVectorDrawable) {
                val animation = drawable as AnimatedVectorDrawable?
                animation?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        animation.start()
                    }
                })
                animation?.start()
            } else if (drawable is AnimatedVectorDrawableCompat) {
                val animation = drawable as AnimatedVectorDrawableCompat
                animation.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        animation.start()
                    }
                })
                animation.start()
            }
        } else {
            if (drawable is AnimatedVectorDrawableCompat) {
                val animation = drawable as AnimatedVectorDrawableCompat
                animation.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        animation.start()
                    }
                })
                animation.start()
            }
        }


        dialog = MaterialAlertDialogBuilder(activity)
            .setView(view)
            .setCancelable(false)
            .show()

    }

    fun filterClockInOrClockOut(onResultFetched: (Boolean) -> Unit, onError: (Throwable?) -> Unit) {
        showDialog()
        ioScope.launch {
            CacheUtils(activity).getUserData {
                val labourServiceRepository = RetrofitProvider.getLabourServiceRepository()
                labourServiceRepository.checkIfUserClockedIn(
                    it.phoneNumber,
                    calculateCurrentDate()
                ).enqueue(
                    object : Callback<List<LabourRecord>> {
                        override fun onResponse(
                            call: Call<List<LabourRecord>>,
                            response: Response<List<LabourRecord>>
                        ) {
                            dialog.cancel()
                            if (response.isSuccessful) {
                                if (response.body() != null) {
                                    if (response.body()!!.isNotEmpty()) {

                                        // here we have the all data for the user this date
                                        laboursList.addAll(response.body()!!)
                                        Log.i(TAG, "onResponse: ${laboursList.toString()}")

                                        // we will filter via address here
                                        val filter = response.body()!!.filter { labour ->
                                            labour.Address.trim() == it.address.trim()
                                        }

                                        if (filter.isNotEmpty()) {
                                            // we don't have any addresses matched us

                                            Log.i(TAG, "onResponse: ${response.body()!!.last()}")
                                            onResultFetched(
                                                response.body()!!.last().clockedOut == "y"
                                            )
                                        } else {
                                            onResultFetched(true)
                                        }

                                    } else {
                                        onResultFetched(true)
                                    }
                                } else {
                                    Log.i(TAG, "238 onResponse: ${response.body()}")
                                    onError(null);
                                }
                            } else {
                                Log.i(TAG, "onResponse: 245")
                                onError(null)
                            }
                        }

                        override fun onFailure(call: Call<List<LabourRecord>>, t: Throwable) {
                            dialog.cancel()

                            onError(t)
                        }

                    })
            }
        }

    }

    private fun mapUserToLabourRecord(user: User, update: String = "n"): LabourRecord {
        val calculateCurrentTime: String = calculateCurrentTime()
        val upld =
            user.phoneNumber.trim() + ":" + calculateDateUniqueNumber() + ":" + user.projectId.trim()
        return LabourRecord(
            user.address,
            user.category,
            user.name,
            user.projectId,
            calculateCurrentTime,
            "19:00:00",
            user.vendorName,
            calculateCurrentDate(),
            user.phoneNumber,
            update,
            upld
        )
    }

    private fun calculateDateUniqueNumber(): Int {
        val month = calculateCurrentDate().split(".")[1].trim().toInt()
        val date = calculateCurrentDate().split(".")[2].trim().toInt()
        return (month - 1).times(30) + date
    }

    private fun generateUPLI(user: User): String {
        return user.phoneNumber.trim() + ":" + calculateDateUniqueNumber() + ":" + user.projectId.trim();
    }


    private fun calculateCurrentTime(): String {
        return if (!Time().calculateTime().contains("GMT", ignoreCase = true)) {
            Log.i(TAG, "calculateCurrentTime: line 291")
            Time().calculateTime().split(",")[1]
                .replace("IST", "").trim();
        } else {
//             try {
                 Log.i(TAG, "calculateCurrentTime: ${Time().calculateTime()}")
                 Time().calculateTime()
                     .substringAfter(",")
                     .trim()
                     .substringBefore("GMT")
                     .trim()

//             }catch (e:Exception){
//                 Log.i(TAG, "calculateCurrentTime: line 304")
//                 Time().calculateTime().split(",")[1]
//                     .replace("IST", "").trim();
//             }
        }
    }


    private fun calculateCurrentDate(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

}