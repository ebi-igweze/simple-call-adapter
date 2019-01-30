package com.igweze.ebi.simplecalladapter

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class Simple<R>(private val call: Call<R>) {

    // support for java
    fun run(responseHandler: SimpleHandler<R?>) = run(responseHandler::accept)
    fun process(responseHandler: SimpleHandler<R?>) = process(responseHandler::accept)

    @Throws(IOException::class)
    fun run() = call.execute()


    fun run(responseHandler: (response: R?, t: Throwable?) -> Unit) {
        // run in the same thread
        try {
            // call and handle response
            val response = call.execute()
            handleResponse(response, responseHandler)

        } catch (t: IOException) {
            responseHandler(null, t)
        }
    }

    fun process(responseHandler: (response: R?, t: Throwable?) -> Unit): Subscription {

        val subscription = Subscription()

        // define callback
        val callback = object : Callback<R> {

            override fun onFailure(call: Call<R>?, t: Throwable?)  {
                if (!subscription.isDisposed()) responseHandler(null, t)
            }

            override fun onResponse(call: Call<R>?, response: Response<R>?) {
                if (!subscription.isDisposed()) handleResponse(response, responseHandler)
            }

        }

        // enqueue network call
        call.enqueue(callback)
        // return subscription
        return subscription
    }

    private fun handleResponse(response: Response<R>?, responseHandler: (R?, Throwable?) -> Unit) {
        if (response?.isSuccessful == true) responseHandler(response.body(), null)
        else {
            if (response?.code() in 400..511) responseHandler(null, HttpException(response))
            else responseHandler(response?.body(), null)
        }
    }
}
