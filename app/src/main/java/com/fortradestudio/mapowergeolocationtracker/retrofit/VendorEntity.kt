package com.fortradestudio.mapowergeolocationtracker.retrofit

import com.google.gson.annotations.SerializedName

data class VendorEntity(
    val Address: String,
    @SerializedName("Cx Name")
    val Customer_Name: String,
    @SerializedName("Project Status")
    val PO_Status: String,
    @SerializedName("Project ID")
    val Project_ID: String,
    val Sn: String,
    @SerializedName("Vendor Name")
    val Vendor_Name: String

)