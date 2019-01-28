package com.igweze.ebi.simplecalladapter.adapter

import com.igweze.ebi.simplecalladapter.Simple
import com.igweze.ebi.simplecalladapter.SimpleCallAdapterFactory
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.lang.reflect.Type

class SimpleCallAdapterFactoryTest {

    @Test
    fun `should return parameterized argument's type when responseType is called`() {
        val factory = SimpleCallAdapterFactory.create()
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
        val factory = SimpleCallAdapterFactory.create()

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
        val factory = SimpleCallAdapterFactory.create()

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