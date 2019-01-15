package com.igweze.ebi.simplecalladapter

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Simple<R>(private val call: Call<R>) {

    fun run(responseHandler: Handler<R?>) {
        // run in the same thread
        try {
            val response = call.execute()
            responseHandler.accept(response.body(), null)
        } catch (t: IOException) {
            responseHandler.accept(null, t)
        }
    }

    fun process(responseHandler: Handler<R?>) {

        // define callback
        val callback = object : Callback<R> {
            override fun onFailure(call: Call<R>?, t: Throwable?) {
                responseHandler.accept(null, t)
            }

            override fun onResponse(call: Call<R>?, response: Response<R>?) {
                responseHandler.accept(response?.body(), null)
            }

        }

        // enqueue network call
        call.enqueue(callback)
    }

    fun run(responseHandler: (response: R?, t: Throwable?) -> Unit) {
        // run in the same thread
        try {
            val response = call.execute()
            responseHandler(response.body(), null)
        } catch (t: IOException) {
            responseHandler(null, t)
        }
    }

    fun process(responseHandler: (response: R?, t: Throwable?) -> Unit) {

        // define callback
        val callback = object : Callback<R> {
            override fun onFailure(call: Call<R>?, t: Throwable?) {
                responseHandler(null, t)
            }

            override fun onResponse(call: Call<R>?, response: Response<R>?) {
                responseHandler(response?.body(), null)
            }

        }

        // enqueue network call
        call.enqueue(callback)
    }
}
