package com.fortradestudio.mapowergeolocationtracker.utils

import android.content.Context
import com.fortradestudio.mapowergeolocationtracker.room.User
import com.fortradestudio.mapowergeolocationtracker.room.UserRepository
import com.fortradestudio.mapowergeolocationtracker.room.VendorAddressDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CacheUtils(
    private val context: Context
) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun getUserData(onUserFetched:(User)->Unit) {
        ioScope.launch {
            val dao = VendorAddressDatabase.getDatabase(context).getUserDao()
            val repo = UserRepository(dao)
            onUserFetched(repo.getUser())
        }
    }

}