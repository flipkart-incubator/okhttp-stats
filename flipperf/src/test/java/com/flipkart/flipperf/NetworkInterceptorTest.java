package com.flipkart.flipperf;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.flipkart.flipperf.interpreter.DefaultInterpreter;
import com.flipkart.flipperf.interpreter.NetworkInterpreter;
import com.flipkart.flipperf.reporter.NetworkEventReporter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.OkHeaders;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 05/05/16 at 7:24 PM.
 * Test for {@link NetworkInterceptor}
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
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEnabled(true)
                .setNetworkInterpreter(networkInterpreter)
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

        //verify responseReceived gets called once
        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorRequest.class), any(NetworkEventReporter.InspectorResponse.class));
    }

    /**
     * Test to verify {@link com.flipkart.flipperf.interpreter.DefaultInterpreter.ForwardingResponseBody}
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
                .code(200)
                .addHeader("Content-Length", "20")
                .body(ResponseBody.create(MediaType.parse("text/plain"), "any text"))
                .build();

        CustomChain customChain = new CustomChain(request, response, null);
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

    /**
     * Test for Response With Content Length
     *
     * @throws IOException
     */
    @Test
    public void testInterceptedResponseWithContentLength() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEnabled(true)
                .setNetworkInterpreter(networkInterpreter)
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
        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorRequest.class), any(NetworkEventReporter.InspectorResponse.class));
    }

    /**
     * Test to verify the request object before and after interception
     *
     * @throws IOException
     */
    @Test
    public void testInterceptedRequest() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEnabled(true)
                .setNetworkInterpreter(networkInterpreter)
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

        //intercepted request object
        Request interceptedRequest = customChain.request();

        //assert method is same
        Assert.assertTrue(interceptedRequest.method().equals(request.method()));
        //assert url is same
        Assert.assertTrue(interceptedRequest.url().toString().equals(request.url().toString()));
        //assert request body is same
        Assert.assertTrue(interceptedRequest.body().equals(request.body()));

    }

    /**
     * Test the request object using {@link MockWebServer}
     *
     * @throws IOException
     */
    @Test
    public void testRequest() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setNetworkInterpreter(networkInterpreter)
                .setEnabled(true)
                .build(RuntimeEnvironment.application);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(networkInterceptor);

        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setBody("Got Response"));

        final byte[] bytes = "Dummy Value".getBytes();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), bytes);

        Request request = new Request.Builder().url(server.url("/"))
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        response.body().string();

        //verify responseReceived got called once
        verify(networkEventReporter, times(1)).responseReceived(any(NetworkEventReporter.InspectorRequest.class), any(NetworkEventReporter.InspectorResponse.class));

        server.shutdown();
    }

    /**
     * Test to verify {@link Response} without content length using {@link MockWebServer}
     *
     * @throws IOException
     */
    @Test
    public void testResponseWithoutContentLength() throws IOException {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(networkEventReporter);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setNetworkInterpreter(networkInterpreter)
                .setEnabled(true)
                .build(RuntimeEnvironment.application);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(networkInterceptor);

        byte[] uncompressedData = "Dummy Value".getBytes();
        byte[] compressedData = compress(uncompressedData);

        MockWebServer server = new MockWebServer();
        server.start();

        //removing content length from the response header
        server.enqueue(new MockResponse()
                .setChunkedBody(new Buffer().write(compressedData),3));

        Request request = new Request.Builder()
                .url(server.url("/"))
                .build();

        //catching this exception due to ForwardingResponseBody
        try {
            Response response = okHttpClient.newCall(request).execute();
            response.body().bytes();
        } catch (IllegalArgumentException ignored) {
        }

        ArgumentCaptor<NetworkEventReporter.InspectorResponse> responseArgumentCaptor = ArgumentCaptor.forClass(NetworkEventReporter.InspectorResponse.class);
        Mockito.verify(networkEventReporter, times(1))
                .responseReceived(any(NetworkEventReporter.InspectorRequest.class), responseArgumentCaptor.capture());
        Assert.assertTrue(responseArgumentCaptor.getValue().responseSize()==compressedData.length);

        server.shutdown();
    }


    /**
     * Tests setter and getter of {@link com.flipkart.flipperf.interpreter.DefaultInterpreter.OkHttpInspectorRequest}
     */
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

        DefaultInterpreter.OkHttpInspectorRequest okHttpInspectorRequest = new DefaultInterpreter.OkHttpInspectorRequest(1, request.url().url(), request.method(), OkHeaders.contentLength(request), request.header("HOST"));

        //assert id is same
        Assert.assertTrue(okHttpInspectorRequest.requestId() == 1);
        //assert url is same
        Assert.assertTrue(okHttpInspectorRequest.url().equals(request.url()));
        //assert content length is same
        Assert.assertTrue(okHttpInspectorRequest.requestSize() == OkHeaders.contentLength(request));
        //assert hostname is same
        Assert.assertTrue(okHttpInspectorRequest.hostName().equals(request.header("HOST")));
        //assert method is same
        Assert.assertTrue(okHttpInspectorRequest.method().equals(request.method()));
    }

    /**
     * Tests setter and getter of {@link com.flipkart.flipperf.interpreter.DefaultInterpreter.OkHttpInspectorResponse}
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
