package com.igweze.ebi.simplecalladapter.adapter

import com.igweze.ebi.simplecalladapter.Simple
import retrofit2.http.GET

interface IHttpService {

    @GET("name")
    fun getName(): Simple<String>

    @GET("place")
    fun getPlace(): Simple<String>
}