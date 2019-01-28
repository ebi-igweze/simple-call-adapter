package com.igweze.ebi.simplecalladapterdemo


import com.igweze.ebi.simplecalladapter.SimpleCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Container {

    private var service: IHttpService? = null

    fun getServiceInstance(): IHttpService {
        return if (service == null) {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://randomuser.me/api/0.8/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(SimpleCallAdapterFactory.create())
                    .build()

            retrofit.create(IHttpService::class.java).also { service = it }
        } else service!!

    }
}