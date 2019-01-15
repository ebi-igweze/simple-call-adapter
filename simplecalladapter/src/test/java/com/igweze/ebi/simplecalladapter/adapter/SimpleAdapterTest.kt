package com.igweze.ebi.simplecalladapter.adapter

import com.igweze.ebi.simplecalladapter.SimpleAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit


class SimpleAdapterTest {

    private val sampleResponse = "Some sample response"
    private val mockServer = MockWebServer()
    private lateinit var httpService: IHttpService

    @Before
    fun setup() {
        // configure mock server and response
        val url = mockServer.url("/")
        val retrofit = Retrofit.Builder()
                .addConverterFactory(StringConverterFactory())
                .addCallAdapterFactory(SimpleAdapterFactory.create())
                .baseUrl(url)
                .build()


             httpService = retrofit.create(IHttpService::class.java)

    }

    @Test
    fun `should extract response from body`() {

        mockServer.enqueue(MockResponse().setBody(sampleResponse))

        // issue request and check results
        httpService.getName().run { codeResponse, throwable ->
            assertNull("an error occured", throwable)
            assertNotNull("response is null", codeResponse)
        }
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }
}