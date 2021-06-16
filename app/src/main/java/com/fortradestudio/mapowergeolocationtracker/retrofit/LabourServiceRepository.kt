  package com.fortradestudio.mapowergeolocationtracker.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LabourServiceRepository {

    @GET(value = "https://sheetdb.io/api/v1/64g4hny4uaim8?sheet=Labor Det.")
    fun getLaboursData():Call<List<LabourEntity>>

    @GET(value = "https://sheetdb.io/api/v1/64g4hny4uaim8?sheet=Site Alloc.")
    fun getVendorAddress():Call<List<VendorEntity>>

    //https://sheetdb.io/api/v1/xm5it8ijcae7d?sheet=output
    //search api
    @GET(value  = "https://sheetdb.io/api/v1/64g4hny4uaim8/search?sheet=output")
    fun checkIfUserClockedIn(@Query("Phone number")ph:String ,
                             @Query("date") date:String):Call<List<LabourRecord>>

    @POST("https://sheetdb.io/api/v1/64g4hny4uaim8?sheet=output")
    fun uploadClockInTime(@Body labourRecord: LabourRecord):Call<LabourRecord>;


    @PATCH("https://sheetdb.io/api/v1/64g4hny4uaim8/UPLI/{UPLI}?sheet=output")
    fun updateUserClockOut(@Path("UPLI")upli:String, @Body labourUploadRecord: LabourUploadRecord):Call<LabourRecord>

}