package com.igweze.ebi.simplecalladapter.adapter

import com.igweze.ebi.simplecalladapter.SimpleAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Type

class SimpleAdapterFactoryTest {

    @Test
    fun test() {
        val factory = SimpleAdapterFactory.create()
        val expectedType = String::class.java
        val mockd: Array<String> = arrayOf()
        val type: Type = mockd.javaClass
        val callAdapter = factory.get(mockd as Type, null, null)

        val actualType = callAdapter?.responseType()
        assertEquals(expectedType, actualType)
    }
}