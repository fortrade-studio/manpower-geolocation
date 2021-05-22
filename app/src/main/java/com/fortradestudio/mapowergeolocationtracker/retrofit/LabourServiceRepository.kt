  package com.fortradestudio.mapowergeolocationtracker.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LabourServiceRepository {

    @GET(value = "https://sheetdb.io/api/v1/xm5it8ijcae7d")
    fun getLaboursData():Call<List<LabourEntity>>

    @GET(value = "https://sheetdb.io/api/v1/xm5it8ijcae7d?sheet=Project-Address-Labor Details")
    fun getVendorAddress():Call<List<VendorEntity>>

    //search api
    @GET(value  = "https://sheetdb.io/api/v1/xqn6tv8ex8n97/search?sheet=output")
    fun checkIfUserClockedIn(@Query("phone number")ph:String ,
                             @Query("date") date:String,
                             @Query("Address") address:String):Call<List<LabourRecord>>

    @POST("https://sheetdb.io/api/v1/xqn6tv8ex8n97?sheet=output")
    fun uploadClockInTime(@Body labourRecord: LabourRecord):Call<LabourRecord>;


    @PATCH("https://sheetdb.io/api/v1/xqn6tv8ex8n97/PID/{PID}?sheet=output")
    fun updateUserClockOut(@Path("PID")pid:String, @Body labourUploadRecord: LabourUploadRecord):Call<LabourRecord>

}