package com.fortradestudio.mapowergeolocationtracker.room

import androidx.lifecycle.LiveData

class VendorAddressRepository(val vendorAddressDao: VendorAddressDao) {

    fun getAllAddresses():LiveData<List<VendorAddresses>>{
        return vendorAddressDao.getAllAddresses()
    }

    fun getAllAddressesSync(): List<VendorAddresses> {
        return vendorAddressDao.getAllAddressesSync()
    }

    fun storeAddress(vararg vendorAddresses: VendorAddresses){
        vendorAddressDao.storeVendorAddress(*vendorAddresses)
    }

    fun clearTable(){
        vendorAddressDao.clearTable()
    }

}