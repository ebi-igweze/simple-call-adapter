package com.igweze.ebi.simplecalladapter.adapter

import com.igweze.ebi.simplecalladapter.Simple
import com.igweze.ebi.simplecalladapter.SimpleAdapterFactory
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.mock
import retrofit2.Call
import java.lang.reflect.Method
import java.lang.reflect.Type

class SimpleAdapterFactoryTest {

    @Test
    fun `should return parameterized argument's type when responseType is called`() {
        val factory = SimpleAdapterFactory.create()
        val expectedType = String::class.java
        // mock generic return type from retrofit
        val anonymous = object {
            fun doSomething(): Simple<String> = mock()
        }

        val type: Type? = anonymous::class.java.methods[0].genericReturnType
        val callAdapter = factory.get(type, null, null)

        val actualType = callAdapter?.responseType()
        assertEquals(expectedType, actualType)
    }

    @Test
    fun `should return null when type is not parameterized or generic`() {
        val factory = SimpleAdapterFactory.create()

        // mock non-generic return type from retrofit
        val anonymous = object {
            fun doSomething(): String = mock()
        }

        val type: Type? = anonymous::class.java.methods[0].genericReturnType
        val callAdapter = factory.get(type, null, null)

        val actualType = callAdapter?.responseType()
        assertNull(actualType)
    }

    @Test
    fun `should return null when enclosing generic type is not 'Simple'`() {
        val factory = SimpleAdapterFactory.create()

        // mock non-generic return type from retrofit
        val anonymous = object {
            fun doSomething(): List<String> = mock()
        }

        val type: Type? = anonymous::class.java.methods[0].genericReturnType
        val callAdapter = factory.get(type, null, null)

        val actualType = callAdapter?.responseType()
        assertNull(actualType)
    }
}