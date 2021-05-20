package com.fortradestudio.mapowergeolocationtracker.room

class UserRepository(
    private val dao: UserDao
) {

    fun getUser()=dao.getUserFromDatabase()


    fun insertUser(user:User){
        if(dao.getSize() == 0) {
            dao.insertToDatabase(user)
        }
    }

}