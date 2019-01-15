package com.igweze.ebi.simplecalladapter

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class SimpleAdapterFactory private constructor() : CallAdapter.Factory() {

    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        return returnType?.let {
            val type = (it as ParameterizedType).actualTypeArguments[0]
            SimpleCallAdapter<Any>(type)
        }
    }

    companion object {
        fun create() = SimpleAdapterFactory()
    }

}
