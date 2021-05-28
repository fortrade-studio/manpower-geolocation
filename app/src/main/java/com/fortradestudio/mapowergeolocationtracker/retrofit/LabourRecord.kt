package com.fortradestudio.mapowergeolocationtracker.retrofit

import com.google.gson.annotations.SerializedName

data class LabourRecord(
    val Address: String,
    val Category: String,
    @SerializedName("Labor Name")
    val Labor_Name: String,
    val PID: String,
    @SerializedName("Time In")
    val Time_In: String,
    @SerializedName("Time out")
    val Time_out: String,
    @SerializedName("Vendor Name")
    val Vendor_Name: String,
    val date: String,
    @SerializedName("phone number")
    val phNo:String,
    @SerializedName("clocked out")
    val clockedOut:String,
    @SerializedName("UPLI")
    val upli:String,
)