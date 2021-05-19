package com.fortradestudio.mapowergeolocationtracker.retrofit

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitProvider {

    companion object{
       private val  BASE_URL =" https://sheetdb.io/api/v1/igf9s2jchz4ow/"
       private var labourService : LabourServiceRepository? =null

            private val loggingInterceptor = HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        };

        private val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .build();

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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