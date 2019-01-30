package com.igweze.ebi.simplecalladapter.adapter

import com.igweze.ebi.simplecalladapter.SimpleCallAdapterFactory
import net.jodah.concurrentunit.Waiter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


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
                .addCallAdapterFactory(SimpleCallAdapterFactory.create())
                .baseUrl(url)
                .build()


             httpService = retrofit.create(IHttpService::class.java)
    }

    @Test
    fun `should extract response from body for 'synchronous-call'`() {

        mockServer.enqueue(MockResponse().setBody(sampleResponse))

        // issue request and check results
        httpService.getName().run { codeResponse, throwable ->
            assertNull("an error occured", throwable)
            assertNotNull("response is null", codeResponse)
            assertEquals(sampleResponse, codeResponse)
        }
    }

    @Test
    fun `should extract response from body for 'asynchronous-call'`() {
        // waiter for concurrent ops
        val waiter = Waiter()

        mockServer.enqueue(MockResponse().setBody(sampleResponse))

        // issue request and check results
        httpService.getPlace().process { codeResponse, throwable ->
            waiter.assertNull(throwable)
            waiter.assertNotNull(codeResponse)
            waiter.assertEquals(sampleResponse, codeResponse)
            // resume main thread
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun `should extract error from body for 'synchronous-call'`() {
        val errorMsg = "Item not found"
        val errorResponse = MockResponse().setResponseCode(404).setBody(errorMsg)
        mockServer.enqueue(errorResponse)

        httpService.getName().run { response, t ->
            assertNull("response is not null", response)
            assertNotNull("error is null", t)
            assertTrue("error is not HttpException", t is HttpException)

            val exception = (t as HttpException)
            assertEquals(404, exception.code())
        }
    }

    @Test
    fun `should extract error from body for 'asynchronous-call'`() {
        val waiter = Waiter()
        val errorMsg = "Item not found"
        val errorResponse = MockResponse().setResponseCode(404).setBody(errorMsg)
        mockServer.enqueue(errorResponse)

        httpService.getPlace().process { response, t ->
            waiter.assertNull(response)
            waiter.assertNotNull(t)
            waiter.assertTrue(t is HttpException)

            val exception = (t as HttpException)
            waiter.assertEquals(404, exception.code())
            waiter.resume()
        }

        waiter.await(1000)
    }


    @Test(expected = TimeoutException::class)
    fun `should not invoke 'process' callback when subscription is disposed`() {
        val waiter = Waiter()

        val msg = "some response"
        val response = MockResponse().setResponseCode(404).setBody(msg).setBodyDelay(2000, TimeUnit.MILLISECONDS)
        mockServer.enqueue(response)

        val subscription = httpService.getPlace().process { r, t ->
            waiter.assertNotNull(r)
            waiter.assertNull(t)
            waiter.resume()
        }

        Thread.sleep(600)

        // dispose subscription
        subscription.dispose()

        // this will throw expected exception
        // because resume will never be called
        // inside of 'process' callback
        waiter.await(3000)
    }

    @Test
    fun `should return retrofit response`() {
        val waiter = Waiter()

        val msg = "some response"
        val response = MockResponse().setBody(sampleResponse)
        mockServer.enqueue(response)

        // get the response
        val responseString = httpService.getName().run()

        // check assertion
        assertEquals(sampleResponse, responseString.body())
    }


    @After
    fun tearDown() {
        mockServer.shutdown()
    }
}