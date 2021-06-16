package com.fortradestudio.mapowergeolocationtracker.retrofit

import com.google.gson.annotations.SerializedName

data class VendorEntity(
    val Address: String,
    @SerializedName("Cx Name")
    val Customer_Name: String,
    @SerializedName("Lati & Longi")
    val Lati_Longi: String,
    @SerializedName("Project Status")
    val PO_Status: String,
    @SerializedName("Project ID")
    val Project_ID: String,
    val Sn: String,
    @SerializedName("Vendor Name")
    val Vendor_Name: String

    //{"Sn":"1","Project ID":"502995","Customer Name":"Juthika Dodle","Address":"","Lati & Longi":"17.5173854,78.4712046",
    // "Vendor Name":"VCA Projects & Interiors Pvt Ltd","PO Status":"PO Closed"}
)