package com.igweze.ebi.simplecalladapter.adapter

import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Retrofit
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type


internal class StringConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        return Converter<ResponseBody, String> { value -> value.string() }
    }


    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody> {
        return Converter<String, RequestBody> { value -> RequestBody.create(MediaType.parse("text/plain"), value) }
    }
}