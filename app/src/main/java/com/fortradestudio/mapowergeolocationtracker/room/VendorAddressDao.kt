package com.fortradestudio.mapowergeolocationtracker.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VendorAddressDao {
    @Query("Select * from vendoraddresses")
    fun getAllAddresses():LiveData<List<VendorAddresses>>

    @Query("Select * from vendoraddresses")
    fun getAllAddressesSync():List<VendorAddresses>

    @Insert
    fun storeVendorAddress(vararg vendorAddresses: VendorAddresses)



    @Delete
    fun deleteVendorAddress(vendorAddresses: VendorAddresses)

    @Query("Delete from vendoraddresses")
    fun clearTable()

}