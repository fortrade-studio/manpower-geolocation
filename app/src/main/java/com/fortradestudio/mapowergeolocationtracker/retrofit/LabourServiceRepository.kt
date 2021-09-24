  package com.fortradestudio.mapowergeolocationtracker.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LabourServiceRepository {

    companion object{
        const val link = "https://sheetdb.io/api/v1/tx2e47jeyvsxm"
    }


    @GET(value = "$link?sheet=Labor Det.")
    fun getLaboursData():Call<List<LabourEntity>>

    @GET(value = "$link?sheet=Site Alloc.")
    fun getVendorAddress():Call<List<VendorEntity>>

    @GET(value  = "$link/search?sheet=output")
    fun checkIfUserClockedIn(@Query("Phone number")ph:String ,
                             @Query("date") date:String):Call<List<LabourRecord>>

    @POST("$link?sheet=output")
    fun uploadClockInTime(@Body labourRecord: LabourRecord):Call<LabourRecord>;


    @PATCH("$link/UPLI/{UPLI}?sheet=output")
    fun updateUserClockOut(@Path("UPLI")upli:String, @Body labourUploadRecord: LabourUploadRecord):Call<LabourRecord>

}