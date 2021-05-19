package com.fortradestudio.mapowergeolocationtracker.viewmodel.homeFragment

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fortradestudio.mapowergeolocationtracker.R
import com.fortradestudio.mapowergeolocationtracker.retrofit.LabourEntity
import com.fortradestudio.mapowergeolocationtracker.retrofit.RetrofitProvider
import com.fortradestudio.mapowergeolocationtracker.retrofit.VendorEntity
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddressDatabase
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddressRepository
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddresses
import com.fortradestudio.mapowergeolocationtracker.utils.Utils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    fun getLabourName(onSuccessApiCallback: (String, String) -> Unit) {
        val labourServiceRepository = RetrofitProvider.getLabourServiceRepository()
        labourServiceRepository.getLaboursData().enqueue(object : Callback<List<LabourEntity>> {
            override fun onResponse(
                call: Call<List<LabourEntity>>,
                response: Response<List<LabourEntity>>
            ) {
                if (response.isSuccessful) {
                    val fromCache = Utils(activity).getFromCache(number_cache_key)
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
                                    current_customer.vendorName
                                )
                            }
                            Log.i(TAG, "onResponse: ${current_customer.vendorName}")
                            getVendorAddresses(
                                current_customer.vendorName.trim(),
                                current_customer.name.trim()
                            );

                        } else {
                            Snackbar.make(view, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<LabourEntity>>, t: Throwable) {
                Snackbar.make(view, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun storeAddressesInDatabase(vararg vendorAddresses: VendorAddresses) {
        ioScope.launch {
            val dao = VendorAddressDatabase.getDatabase(activity).getDao()
            val repo = VendorAddressRepository(dao)
            repo.clearTable();

            repo.storeAddress(*vendorAddresses)
        }
    }

    public fun getVendorAddresses(vendorName: String, labourName: String) {
        ioScope.launch {
            val labourServiceRepository = RetrofitProvider.getLabourServiceRepository()
            labourServiceRepository.getVendorAddress().enqueue(object :
                Callback<List<VendorEntity>> {
                override fun onResponse(
                    call: Call<List<VendorEntity>>,
                    response: Response<List<VendorEntity>>
                ) {
                    if (response.isSuccessful) {

                        if (response.body() != null) {

                            val tempList = ArrayList<VendorEntity>()

                            for (v in response.body()!!) {
                                // here we will get the vendor ka address
                                if (v.Vendor_Name.trim() == vendorName.trim()) {
                                    // we found our vendor yaaay
                                    tempList.add(v)
                                    vendorAddressesLiveData.postValue(tempList)
                                    storeAddressesInDatabase(mapVendorEntityToVendorAddress(labourName,v))
                                    Log.i(TAG, "onResponse: ${v.Address}")
                                }
                            }

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


    private fun mapVendorEntityToVendorAddress(
        labourName: String,
        v: VendorEntity
    ): VendorAddresses {
        val latiLongi = v.Lati_Longi
        val split = latiLongi.split(",")
//        val latitude = split[0]
//        val longitude = split[1]

        return VendorAddresses(
            labourName = labourName,
            address = v.Address,
            vendorName = v.Vendor_Name,
            projectId = v.Project_ID,
            latitude = 27.02,
            longitude = 88.72
        )
    }

}