package com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment

import android.app.Activity
import android.location.Location
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationDao
import com.fortradestudio.mapowergeolocationtracker.locationsUtils.LocationUtils
import com.fortradestudio.mapowergeolocationtracker.retrofit.LabourEntity
import com.fortradestudio.mapowergeolocationtracker.retrofit.RetrofitProvider
import com.fortradestudio.mapowergeolocationtracker.retrofit.VendorEntity
import com.fortradestudio.mapowergeolocationtracker.room.*
import com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.abs

class HomeFragmentViewModel(
    val view: View,
    val activity: Activity
) : ViewModel() {

    companion object {
        private const val TAG = "HomeFragment"
        const val notification_Cache = "notificationcache"
        private const val number_cache_key = "phoneNumber"
    }

    val ioScope = CoroutineScope(Dispatchers.IO)
    val mainScope = CoroutineScope(Dispatchers.Main)

    val vendorAddressesLiveData = MutableLiveData<List<VendorEntity>>()

    fun getLabourName(onSuccessApiCallback: (String, String,String) -> Unit) {
        val labourServiceRepository = RetrofitProvider.getLabourServiceRepository()
        labourServiceRepository.getLaboursData().enqueue(object : Callback<List<LabourEntity>> {
            override fun onResponse(
                call: Call<List<LabourEntity>>,
                response: Response<List<LabourEntity>>
            ) {
                if (response.isSuccessful) {
                    Log.i(TAG, "onResponse: success")
                    val fromCache =
                        FirebaseAuth.getInstance().currentUser?.phoneNumber?.removePrefix("+91")
                    if (fromCache != null) {
                        val current_customer: LabourEntity? = response.body()?.find {
                            it.phNo.trim() == fromCache.trim()
                        }
                        if (current_customer != null) {
                            // found the customer display there name now also we need to find the
                            // vendor name to load site addresses
                            mainScope.launch {
                                onSuccessApiCallback(
                                    current_customer.name,
                                    current_customer.vendorName,
                                    current_customer.category
                                )
                            }
                            Log.i(TAG, "onResponse: ${current_customer.vendorName}")
                            getVendorAddresses(
                                current_customer.vendorName.trim(),
                                current_customer.name.trim(),
                                current_customer.projectId
                            );

                        } else {
                            Log.i(TAG, "onResponse: 77")
                            Snackbar.make(view, R.string.no_such_user_data, Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                } else {
                    Log.e(TAG, "onResponse: this is 80")
                    Snackbar.make(view, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<LabourEntity>>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
                Snackbar.make(view, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun storeAddressesInDatabase(vararg vendorAddresses: VendorAddresses) {
        ioScope.launch {
            val dao = VendorAddressDatabase.getDatabase(activity).getDao()
            val repo = VendorAddressRepository(dao)
            repo.storeAddress(*vendorAddresses)
        }
    }

    // we need to filter out according to the vendor registered for the user
    public fun getVendorAddresses(
        vendorName: String,
        labourName: String,
        projectId:String,
        labourEntity: LabourEntity? = null
    ) {
        ioScope.launch {
            val labourServiceRepository = RetrofitProvider.getLabourServiceRepository()
            labourServiceRepository.getVendorAddress().enqueue(object :
                Callback<List<VendorEntity>> {
                override fun onResponse(
                    call: Call<List<VendorEntity>>,
                    response: Response<List<VendorEntity>>
                ) {
                    if (response.isSuccessful) {

                        ioScope.launch {
                            val dao = VendorAddressDatabase.getDatabase(activity).getDao()
                            val repo = VendorAddressRepository(dao)
                            repo.clearTable();
                        }


                        if (response.body() != null) {

                            val tempList = ArrayList<VendorEntity>()
                            for (v in response.body()!!) {
                                // here we will get the vendor ka address
                                if (v.Vendor_Name.trim() == vendorName.trim() && v.Project_ID.trim() != projectId.trim() && poFilter(v)) {
                                  // this is the case when the db manager don't have the project id of the user
                                    // we found our vendor yaaay
                                    // also we need to filter those whose po is closed

                                    tempList.add(v)
                                    Log.i(TAG,v.toString())
                                    vendorAddressesLiveData.postValue(tempList)

                                    val mapVendorEntityToVendorAddress =
                                        mapVendorEntityToVendorAddress(
                                            labourName,
                                            v
                                        )

                                    if(mapVendorEntityToVendorAddress.latitude!=-1.0 || mapVendorEntityToVendorAddress.longitude!=-1.0){
                                        storeAddressesInDatabase(mapVendorEntityToVendorAddress)
                                    }
                                }else if (v.Vendor_Name.trim() == vendorName.trim() && v.Project_ID.trim() == projectId.trim() && poFilter(v)){
                                    // this is the case when the database manager have the project id
                                    tempList.add(v)
                                    vendorAddressesLiveData.postValue(tempList)

                                    val mapVendorEntityToVendorAddress =
                                        mapVendorEntityToVendorAddress(
                                            labourName,
                                            v
                                        )

                                    if(mapVendorEntityToVendorAddress.latitude!=-1.0 || mapVendorEntityToVendorAddress.longitude!=-1.0){
                                        storeAddressesInDatabase(mapVendorEntityToVendorAddress)
                                    }
                                }
                            }
                        } else {
                            Snackbar.make(
                                view,
                                activity.getString(R.string.no_vendor_data_found) + " $vendorName",
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }

                    } else {
                        Snackbar.make(view, R.string.vendor_address_error, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<VendorEntity>>, t: Throwable) {
                    Snackbar.make(view, R.string.vendor_address_error, Snackbar.LENGTH_LONG)
                        .show()
                }

            })
        }
    }


    // this error was happening because the user was using PO CLOSED before as they key
    private fun poFilter(vendor: VendorEntity): Boolean {
        return !vendor.PO_Status.trim()
            .contains("Closed", true)
    }

    fun mapVendorEntityToVendorAddress(
        labourName: String,
        v: VendorEntity
    ): VendorAddresses {
        val latiLongi = Utils(activity).getLocationFromAddress(v.Address)
        if(latiLongi == null){
            throw NullPointerException()
        }
        try {
            val latitude = latiLongi.latitude
            val longitude = latiLongi.longitude

            return VendorAddresses(
                labourName = labourName,
                address = v.Address,
                vendorName = v.Vendor_Name,
                projectId = v.Project_ID,
                latitude = latitude,
                longitude = longitude
            )

        }catch (e:Exception){

            return VendorAddresses(
                labourName = labourName,
                address = v.Address,
                vendorName = v.Vendor_Name,
                projectId = v.Project_ID,
                latitude = -1.0,
                longitude = -1.0
            )
        }

    }


}