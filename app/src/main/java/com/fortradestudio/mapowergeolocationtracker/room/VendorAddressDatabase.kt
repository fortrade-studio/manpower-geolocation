package com.fortradestudio.mapowergeolocationtracker.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VendorAddresses::class, User::class],version = 8,exportSchema = true)
abstract class VendorAddressDatabase :RoomDatabase() {

    abstract fun getDao():VendorAddressDao
    abstract fun getUserDao():UserDao

    companion object{
        private var vendorAddresses:VendorAddressDatabase?=null

        @Volatile private var temp:VendorAddressDatabase? = vendorAddresses;

        public fun getDatabase(context: Context):VendorAddressDatabase{
            if (temp!=null){
                return temp as VendorAddressDatabase;
            }else{
                 vendorAddresses = Room
                     .databaseBuilder(context,VendorAddressDatabase::class.java,"VendorAddresses")
                     .fallbackToDestructiveMigration()
                     .build()
            }
            temp = vendorAddresses;
            return temp as VendorAddressDatabase;
        }

    }

}