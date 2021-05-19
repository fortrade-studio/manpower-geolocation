package com.fortradestudio.mapowergeolocationtracker.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VendorAddresses(
    @PrimaryKey(autoGenerate = true)
    var uId:Int?=null,
    var labourName:String,
    var address:String,
    var vendorName:String,
    var projectId:String,
    var longitude:Double,
    var latitude:Double
)
