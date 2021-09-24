package com.fortradestudio.mapowergeolocationtracker.retrofit

import com.google.gson.annotations.SerializedName

data class LabourEntity(
    @SerializedName("Category")
    var category: String,
    @SerializedName("Manpower")
    var name: String,
    @SerializedName("Ph No")
    var phNo: String,
    @SerializedName("Sn")
    var sn: String,
    @SerializedName("Vendor Name")
    var vendorName : String,
    @SerializedName("Project Id")
    var projectId:String,
){
}