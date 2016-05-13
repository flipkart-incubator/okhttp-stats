package com.flipkart.flipperf;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.flipkart.flipperf.response.ResponseHandler;
import com.squareup.okhttp.Connection;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import okio.Buffer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by anirudh.r on 05/05/16 at 7:24 PM.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkInterceptorTest {

    private static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(buf);
        out.write(data);
        out.close();
        return buf.toByteArray();
    }

    /**
     * Test to verify the response object before and after interception
     *
     * @throws IOException
     */
    @Test
    public void testInterceptedResponse() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setReporterEnabled(true)
                .setHandler(null)
                .setNetworkManager(networkManager)
                .setEventReporter(networkEventReporter)
                .build(RuntimeEnvironment.application);

        //return true whenever networkEventReporter.isReporterEnabled() is called
        when(networkEventReporter.isReporterEnabled()).thenReturn(true);

        Uri requestUri = Uri.parse("http://www.flipkart.com");
        String requestText = "Test Request";

        //creating request
        Request request = new Request.Builder()
                .url(requestUri.toString())
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

        CustomChain customChain = new CustomChain(request, response, null);
        networkInterceptor.intercept(customChain);

        //intercepted request object
        Response interceptedResponse = customChain.proceed(request);

        //assert response code
        Assert.assertTrue(interceptedResponse.code() == 200);
        //assert request object
        Assert.assertTrue(interceptedResponse.request() == request);
        //assert protocol used
        Assert.assertTrue(interceptedResponse.protocol() == Protocol.HTTP_1_1);

        //verify requestToBeSent gets called once
        verify(networkEventReporter, times(1)).requestToBeSent(any(NetworkEventReporter.InspectorRequest.class));

        //verify responseReceived gets called once
        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorResponse.class));

        //verify responseDataReceived does not gets called as response has content length
        verify(networkEventReporter, times(0)).responseDataReceived(any(NetworkEventReporter.InspectorResponse.class), anyInt());
    }

    /**
     * Test to verify {@link NetworkInterceptor.ForwardingResponseBody}
     *
     * @throws IOException
     */
    @Test
    public void testResponseBody() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setReporterEnabled(true)
                .setHandler(null)
                .setNetworkManager(networkManager)
                .setEventReporter(networkEventReporter)
                .build(RuntimeEnvironment.application);

        when(networkEventReporter.isReporterEnabled()).thenReturn(true);

        Uri requestUri = Uri.parse("http://www.flipkart.com");
        String requestText = "Test Request";

        //creating request
        Request request = new Request.Builder()
                .url(requestUri.toString())
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

        CustomChain customChain = new CustomChain(request, response, null);
        networkInterceptor.intercept(customChain);

        //intercepted request object
        Response interceptedResponse = customChain.proceed(request);
        NetworkInterceptor.ForwardingResponseBody forwardingResponseBody = new NetworkInterceptor.ForwardingResponseBody(interceptedResponse.body(), interceptedResponse.body().byteStream());

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

    /**
     * Test for Response With Content Length
     *
     * @throws IOException
     */
    @Test
    public void testInterceptedResponseWithContentLength() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setReporterEnabled(true)
                .setHandler(null)
                .setNetworkManager(networkManager)
                .setEventReporter(networkEventReporter)
                .build(RuntimeEnvironment.application);

        when(networkEventReporter.isReporterEnabled()).thenReturn(true);

        Uri requestUri = Uri.parse("http://www.flipkart.com");
        String requestText = "Test Request";

        //creating request
        Request request = new Request.Builder()
                .url(requestUri.toString())
                .method("POST", RequestBody.create(MediaType.parse("text/plain"), requestText))
                .build();

        //creating response
        Response response = new Response.Builder()
                .request(request)
                .header("Content-Length", "8")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(ResponseBody.create(MediaType.parse("text/plain"), "any text"))
                .build();

        CustomChain customChain = new CustomChain(request, response, null);
        networkInterceptor.intercept(customChain);

        //intercepted request object
        Response interceptedResponse = customChain.proceed(request);

        //assert response code
        Assert.assertTrue(interceptedResponse.body().contentLength() == 8);

        //verify responseReceived gets called once
        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorResponse.class));
    }

    /**
     * Test to verify the request object before and after interception
     *
     * @throws IOException
     */
    @Test
    public void testInterceptedRequest() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkManager networkManager = mock(NetworkManager.class);
        when(networkEventReporter.isReporterEnabled()).thenReturn(true);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setReporterEnabled(true)
                .setHandler(null)
                .setNetworkManager(networkManager)
                .setEventReporter(networkEventReporter)
                .build(RuntimeEnvironment.application);

        Uri requestUri = Uri.parse("http://www.flipkart.com");
        String requestText = "Test Request";

        //creating request
        Request request = new Request.Builder()
                .url(requestUri.toString())
                .method("POST", RequestBody.create(MediaType.parse("text/plain"), requestText))
                .build();

        //creating response
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .addHeader("Content-Length", "20")
                .code(200)
                .body(ResponseBody.create(MediaType.parse("text/plain"), "any text"))
                .build();

        CustomChain customChain = new CustomChain(request, response, null);
        networkInterceptor.intercept(customChain);

        //assert network event reported is enabled
        Assert.assertTrue(networkEventReporter.isReporterEnabled());

        //intercepted request object
        Request interceptedRequest = customChain.request();

        //assert method is same
        Assert.assertTrue(interceptedRequest.method().equals(request.method()));
        //assert url is same
        Assert.assertTrue(interceptedRequest.urlString().equals(request.urlString()));
        //assert request body is same
        Assert.assertTrue(interceptedRequest.body().equals(request.body()));

        //verify requestToBeSent gets called once
        verify(networkEventReporter, times(1)).requestToBeSent(any(NetworkEventReporter.InspectorRequest.class));
    }

    /**
     * Test the request object using {@link MockWebServer}
     *
     * @throws IOException
     */
    @Test
    public void testRequest() throws IOException {
        NetworkManager networkManager = mock(NetworkManager.class);
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        Mockito.when(networkEventReporter.isReporterEnabled()).thenReturn(true);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEventReporter(networkEventReporter)
                .setNetworkManager(networkManager)
                .setReporterEnabled(true)
                .build(RuntimeEnvironment.application);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(networkInterceptor);

        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setBody("Got Response"));

        final byte[] bytes = "Dummy Value".getBytes();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), bytes);

        Request request = new Request.Builder().url(server.getUrl("/"))
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        response.body().string();

        //verify responseReceived got called once
        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorResponse.class));

        server.shutdown();
    }

    /**
     * Test to verify {@link Response} without content length using {@link MockWebServer}
     *
     * @throws IOException
     */
    @Test
    public void testResponseWithoutContentLength() throws IOException {
        NetworkManager networkManager = mock(NetworkManager.class);
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        Mockito.when(networkEventReporter.isReporterEnabled()).thenReturn(true);
        NetworkInterceptor.ForwardingResponseBody forwardingResponseBody;

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEventReporter(networkEventReporter)
                .setNetworkManager(networkManager)
                .setReporterEnabled(true)
                .build(RuntimeEnvironment.application);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(networkInterceptor);

        byte[] uncompressedData = "Dummy Value".getBytes();
        byte[] compressedData = compress(uncompressedData);

        MockWebServer server = new MockWebServer();
        server.start();

        //removing content length from the response header
        server.enqueue(new MockResponse()
                .setBody(new Buffer().write(compressedData))
                .removeHeader("Content-Length"));

        Request request = new Request.Builder()
                .url(server.url("/"))
                .build();

        //catching this exception due to ForwardingResponseBody
        try {
            Response response = okHttpClient.newCall(request).execute();
        } catch (IllegalArgumentException ignored) {
        }

        // Verify that interpretResponseStream gets called once
        Mockito.verify(networkEventReporter, times(1))
                .interpretResponseStream(any(InputStream.class), any(ResponseHandler.class));

        server.shutdown();
    }

    /**
     * Test to verify {@link Response} with content length using {@link MockWebServer}
     *
     * @throws IOException
     */
    @Test
    public void testResponseWithContentLength() throws IOException {
        NetworkManager networkManager = mock(NetworkManager.class);
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        Mockito.when(networkEventReporter.isReporterEnabled()).thenReturn(true);
        NetworkInterceptor.ForwardingResponseBody forwardingResponseBody;

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEventReporter(networkEventReporter)
                .setNetworkManager(networkManager)
                .setReporterEnabled(true)
                .build(RuntimeEnvironment.application);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(networkInterceptor);

        byte[] uncompressedData = "Dummy Value".getBytes();

        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse()
                .setBody(new Buffer().write(uncompressedData)));

        Request request = new Request.Builder()
                .url(server.url("/"))
                .build();

        Response response = okHttpClient.newCall(request).execute();

        // Verify that interpretResponseStream does not gets called since response had content length
        Mockito.verify(networkEventReporter, times(0))
                .interpretResponseStream(any(InputStream.class), any(ResponseHandler.class));

        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorResponse.class));

        server.shutdown();
    }

    @Test
    public void testOkHttpInspectorRequest() throws Exception {

        Uri requestUri = Uri.parse("http://www.flipkart.com");
        String requestText = "Test Request";

        Request request = new Request.Builder()
                .url(requestUri.toString())
                .method("POST", RequestBody.create(MediaType.parse("text/plain"), requestText))
                .addHeader("Content-Length", "20")
                .addHeader("HOST", "flipkart")
                .build();

        NetworkInterceptor.OkHttpInspectorRequest okHttpInspectorRequest = new NetworkInterceptor.OkHttpInspectorRequest(1, request.urlString(), request.method(), request.header("Content-Length"), request.header("HOST"));

        //assert id is same
        Assert.assertTrue(okHttpInspectorRequest.requestId() == 1);
        //assert url is same
        Assert.assertTrue(okHttpInspectorRequest.url().equals(request.urlString()));
        //assert content length is same
        Assert.assertTrue(okHttpInspectorRequest.requestSize().equals(request.header("Content-Length")));
        //assert hostname is same
        Assert.assertTrue(okHttpInspectorRequest.hostName().equals(request.header("HOST")));
        //assert method is same
        Assert.assertTrue(okHttpInspectorRequest.method().equals(request.method()));
    }

    @Test
    public void testOkHttpInspectorResponse() throws Exception {

        NetworkInterceptor.OkHttpInspectorResponse okHttpInspectorResponse = new NetworkInterceptor.OkHttpInspectorResponse(1, 200, "20", 2);

        //assert id is same
        Assert.assertTrue(okHttpInspectorResponse.requestId() == 1);
        //assert content length is same
        Assert.assertTrue(okHttpInspectorResponse.responseSize().equals("20"));
        //assert response time is same
        Assert.assertTrue(okHttpInspectorResponse.responseTime() == 2);
        //assert statuscode is same
        Assert.assertTrue(okHttpInspectorResponse.statusCode() == 200);
    }

    private static class CustomChain implements Interceptor.Chain {
        private final Request mRequest;
        private final Response mResponse;
        @Nullable
        private final Connection mConnection;

        public CustomChain(Request request, Response response, @Nullable Connection connection) {
            mRequest = request;
            mResponse = response;
            mConnection = connection;
        }

        @Override
        public Request request() {
            return mRequest;
        }

        @Override
        public Response proceed(Request request) throws IOException {
            if (mRequest != request) {
                throw new IllegalArgumentException(
                        "Expected " + System.identityHashCode(mRequest) +
                                "; got " + System.identityHashCode(request));
            }
            return mResponse;
        }

        @Override
        public Connection connection() {
            return mConnection;
        }
    }
}
