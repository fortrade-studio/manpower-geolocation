package com.fortradestudio.mapowergeolocationtracker.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

interface LabourServiceRepository {

    @GET(value = "https://sheetdb.io/api/v1/igf9s2jchz4ow")
    fun getLaboursData():Call<List<LabourEntity>>

    @GET(value = "https://sheetdb.io/api/v1/igf9s2jchz4ow?sheet=Sheet3")
    fun getVendorAddress():Call<List<VendorEntity>>

}