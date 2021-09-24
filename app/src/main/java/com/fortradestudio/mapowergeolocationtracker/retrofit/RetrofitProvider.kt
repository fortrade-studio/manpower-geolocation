package com.fortradestudio.mapowergeolocationtracker.retrofit

import com.squareup.okhttp.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class RetrofitProvider {

    companion object{
       private const val  BASE_URL ="https://sheetdb.io/api/v1/tx2e47jeyvsxm/"
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