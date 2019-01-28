package com.igweze.ebi.simplecalladapterdemo

import com.igweze.ebi.simplecalladapter.Simple
import retrofit2.http.Query

interface IHttpService {

    fun getUsers(@Query("results") result: Int): Simple<Response<List<User>>>

}