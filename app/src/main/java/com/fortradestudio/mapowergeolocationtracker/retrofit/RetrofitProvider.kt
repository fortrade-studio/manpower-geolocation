package com.fortradestudio.mapowergeolocationtracker.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitProvider {

    companion object{
       private const val  BASE_URL ="https://sheetdb.io/api/v1/xm5it8ijcae7d/"
       private var labourService : LabourServiceRepository? =null


        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun getLabourServiceRepository():LabourServiceRepository{
            var temp : LabourServiceRepository? = labourService
            if(temp!=null){
                return labourService!!;
            }
            else {
               labourService= retrofit.create(LabourServiceRepository::class.java)
               temp = labourService;
            }

            return temp!!;
        }
    }

}