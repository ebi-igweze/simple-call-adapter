package com.igweze.ebi.simplecalladapter.adapter;

import com.igweze.ebi.simplecalladapter.SimpleHandler;
import com.igweze.ebi.simplecalladapter.SimpleCallAdapterFactory;
import com.igweze.ebi.simplecalladapter.Subscription;

import net.jodah.concurrentunit.Waiter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.HttpException;
import retrofit2.Retrofit;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class SimpleAdapterJavaTest {


    private String sampleResponse = "Some sample response";
    private MockWebServer mockServer = new MockWebServer();
    private IHttpService httpService;

    @Before
    public void setup() {
        // configure mock server and response
        HttpUrl url = mockServer.url("/");
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(new StringConverterFactory())
                .addCallAdapterFactory(SimpleCallAdapterFactory.create())
                .baseUrl(url)
                .build();


        httpService = retrofit.create(IHttpService.class);
    }

    @Test
    public void should_extract_response_from_body_for_synchronous_call() {

        mockServer.enqueue(new MockResponse().setBody(sampleResponse));

        // issue request and check results
        httpService.getName().run(new SimpleHandler<String>() {
            @Override
            public void accept(String codeResponse, Throwable throwable) {
                assertNull("an error occured", throwable);
                assertNotNull("response is null", codeResponse);
                assertEquals(sampleResponse, codeResponse);
            }
        });
    }

    @Test
    public void should_extract_response_from_body_for_asynchronous_call() throws TimeoutException {
        // waiter for concurrent ops
        Waiter waiter = new Waiter();

        mockServer.enqueue(new MockResponse().setBody(sampleResponse));

        // issue request and check results
        httpService.getPlace().process((codeResponse, throwable) -> {
            waiter.assertNull(throwable);
            waiter.assertNotNull(codeResponse);
            waiter.assertEquals(sampleResponse, codeResponse);
            // resume main thread
            waiter.resume();
        });

        waiter.await(1000L);
    }

    @Test
    public void should_extract_error_from_body_for_synchronous_call() {
        String errorMsg = "Item not found";
        MockResponse errorResponse = new MockResponse().setResponseCode(404).setBody(errorMsg);
        mockServer.enqueue(errorResponse);

        httpService.getName().run ((response, t) -> {
            assertNull("response is not null", response);
            assertNotNull("error is null", t);
            assertTrue("error is not HttpException", t instanceof HttpException);

            HttpException exception = (HttpException) t;
            assertEquals(404, exception.code());
        });
    }

    @Test
    public void should_extract_error_from_body_for_asynchronous_call() throws TimeoutException {
        Waiter waiter = new Waiter();
        String errorMsg = "Item not found";
        MockResponse errorResponse = new MockResponse().setResponseCode(404).setBody(errorMsg);
        mockServer.enqueue(errorResponse);

        httpService.getPlace().process ((response, t) -> {
            waiter.assertNull(response);
            waiter.assertNotNull(t);
            waiter.assertTrue(t instanceof HttpException);

            HttpException exception = (HttpException) t;
            waiter.assertEquals(404, exception.code());
            waiter.resume();
        });

        waiter.await(1000);
    }

    @Test(expected = TimeoutException.class)
    public void should_not_invoke_process_callback_when_subscription_is_disposed() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();

        String msg = "some response";
        MockResponse response = new MockResponse().setResponseCode(404).setBody(msg).setBodyDelay(2000, TimeUnit.MILLISECONDS);
        mockServer.enqueue(response);

        Subscription subscription = httpService.getPlace().process ((r, t) -> {
                waiter.assertNotNull(r);
            waiter.assertNull(t);
             waiter.resume();
        });

        Thread.sleep(600);

        // dispose subscription
        subscription.dispose();

        // this will throw expected exception
        // because resume will never be called
        // inside of 'process' callback
        waiter.await(3000);
    }

    @After
    public void tearDown() throws IOException {
        mockServer.shutdown();
    }
}
