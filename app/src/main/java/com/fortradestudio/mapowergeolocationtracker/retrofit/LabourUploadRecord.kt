package com.fortradestudio.mapowergeolocationtracker.retrofit

import com.google.gson.annotations.SerializedName

data class LabourUploadRecord(
    @SerializedName("Time out")
    val Time_out: String,
    @SerializedName("clocked out")
    val clockedOut:String,
    @SerializedName("UPLI")
    val upli:String
)