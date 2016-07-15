package com.flipkart.okhttpstats.interpreter;

import android.net.Uri;

import com.flipkart.okhttpstats.NetworkInterceptor;
import com.flipkart.okhttpstats.NetworkInterceptorTest;
import com.flipkart.okhttpstats.reporter.NetworkEventReporter;
import com.flipkart.okhttpstats.toolbox.Utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 15/07/16.
 * Test for {@link DefaultInterpreter}
 */
public class DefaultInterpreterTest {

    /**
     * Test to verify that {@link NetworkEventReporter#responseReceived(NetworkEventReporter.InspectorRequest, NetworkEventReporter.InspectorResponse)} gets
     * called whenever we receive a response.
     *
     * @throws Exception
     */
    @Test
    public void testResponseReceivedCalledForEventReporter() throws Exception {

        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        DefaultInterpreter defaultInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor.TimeInfo info = new NetworkInterceptor.TimeInfo();
        info.mStartTime = 10;
        info.mEndTime = 20;

        Request request = new Request.Builder()
                .url("http://www.flipkart.com")
                .build();

        Response response = new Response.Builder()
                .code(200)
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .addHeader("Content-Length", "20")
                .build();

        defaultInterpreter.interpretResponseStream(1, info, request, response);

        //verify that responseReceived gets called once
        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorRequest.class), any(NetworkEventReporter.InspectorResponse.class));
    }

    /**
     * Test to verify that {@link NetworkEventReporter#httpExchangeError(NetworkEventReporter.InspectorRequest, IOException)} gets called
     * whenever any error occurs.
     *
     * @throws Exception
     */
    @Test
    public void testOnHttpExchangeErrorGetsCalled() throws Exception {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        DefaultInterpreter defaultInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor.TimeInfo info = new NetworkInterceptor.TimeInfo();
        info.mStartTime = 10;
        info.mEndTime = 20;

        Request request = new Request.Builder()
                .url("http://www.flipkart.com")
                .build();

        IOException exception = new IOException("Test error");

        defaultInterpreter.interpretError(1, info, request, exception);

        //verify that httpExchangeError gets called once
        verify(networkEventReporter, times(1)).httpExchangeError(any(NetworkEventReporter.InspectorRequest.class), eq(exception));
    }

    /**
     * Tests setter and getter of {@link com.flipkart.okhttpstats.interpreter.DefaultInterpreter.OkHttpInspectorRequest}
     */
    @Test
    public void testOkHttpInspectorRequest() throws Exception {

        String requestText = "Test Request";

        Request request = new Request.Builder()
                .url("http://www.flipkart.com")
                .method("POST", RequestBody.create(MediaType.parse("text/plain"), requestText))
                .addHeader("Content-Length", "20")
                .addHeader("HOST", "flipkart")
                .build();

        DefaultInterpreter.OkHttpInspectorRequest okHttpInspectorRequest = new DefaultInterpreter.OkHttpInspectorRequest(1, request.url().url(), request.method(), Utils.contentLength(request), request.header("HOST"));

        //assert id is same
        Assert.assertTrue(okHttpInspectorRequest.requestId() == 1);
        //assert url is same
        Assert.assertTrue(okHttpInspectorRequest.url().equals(request.url().url()));
        //assert content length is same
        Assert.assertTrue(okHttpInspectorRequest.requestSize() == Utils.contentLength(request));
        //assert hostname is same
        Assert.assertTrue(okHttpInspectorRequest.hostName().equals(request.header("HOST")));
        //assert method is same
        Assert.assertTrue(okHttpInspectorRequest.method().equals(request.method()));
    }

    /**
     * Tests setter and getter of {@link com.flipkart.okhttpstats.interpreter.DefaultInterpreter.OkHttpInspectorResponse}
     */
    @Test
    public void testOkHttpInspectorResponse() throws Exception {

        DefaultInterpreter.OkHttpInspectorResponse okHttpInspectorResponse = new DefaultInterpreter.OkHttpInspectorResponse(1, 200, 20, 2, 3);

        //assert id is same
        Assert.assertTrue(okHttpInspectorResponse.requestId() == 1);
        //assert content length is same
        Assert.assertTrue(okHttpInspectorResponse.responseSize() == 20);
        //assert response time is same
        Assert.assertTrue(okHttpInspectorResponse.startTime() == 2);
        //assert statuscode is same
        Assert.assertTrue(okHttpInspectorResponse.statusCode() == 200);
    }

    /**
     * Test to verify {@link com.flipkart.okhttpstats.interpreter.DefaultInterpreter.ForwardingResponseBody}
     *
     * @throws IOException
     */
    @Test
    public void testResponseBody() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEnabled(true)
                .setNetworkInterpreter(networkInterpreter)
                .build();

        String requestText = "Test Request";

        //creating request
        Request request = new Request.Builder()
                .url("http://www.flipkart.com")
                .method("POST", RequestBody.create(MediaType.parse("text/plain"), requestText))
                .build();

        //creating response
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .addHeader("Content-Length", "20")
                .body(ResponseBody.create(MediaType.parse("text/plain"), "any text"))
                .build();

        NetworkInterceptorTest.CustomChain customChain = new NetworkInterceptorTest.CustomChain(request, response, null);
        networkInterceptor.intercept(customChain);

        //intercepted request object
        Response interceptedResponse = customChain.proceed(request);
        DefaultInterpreter.ForwardingResponseBody forwardingResponseBody = new DefaultInterpreter.ForwardingResponseBody(interceptedResponse.body(), interceptedResponse.body().byteStream());

        interceptedResponse = interceptedResponse.newBuilder().body(forwardingResponseBody).build();

        //assert the response body of response
        Assert.assertTrue(interceptedResponse.body() == forwardingResponseBody);
        //assert the content type
        Assert.assertTrue(interceptedResponse.body().contentType() == forwardingResponseBody.contentType());
        //assert the content length
        Assert.assertTrue(interceptedResponse.body().contentLength() == forwardingResponseBody.contentLength());
        //assert the source
        Assert.assertTrue(interceptedResponse.body().source() == forwardingResponseBody.source());
    }
}
