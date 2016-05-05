package com.flipkart.flipperf;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Connection;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by anirudh.r on 05/05/16 at 7:24 PM.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkInterceptorTest {

    NetworkEventReporter networkEventReporter;

    /**
     * Test to verify the request object before and after interception
     *
     * @throws IOException
     */
    @Test
    public void testInterceptedRequest() throws IOException {
        networkEventReporter = mock(NetworkEventReporter.class);
        NetworkInterceptor networkInterceptor = new NetworkInterceptor(RuntimeEnvironment.application);

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
                .body(ResponseBody.create(MediaType.parse("text/plain"), "any text"))
                .build();

        CustomChain customChain = new CustomChain(request, response, null);
        networkInterceptor.intercept(customChain);
        when(networkEventReporter.isReporterEnabled()).thenReturn(true);

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

    }

    /**
     * Test to verify the response object before and after interception
     *
     * @throws IOException
     */
    @Test
    public void testInterceptedResponse() throws IOException {
        networkEventReporter = mock(NetworkEventReporter.class);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor(RuntimeEnvironment.application);
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
    }

    /**
     * Test to verify {@link com.flipkart.flipperf.NetworkInterceptor.ForwardingResponseBody}
     *
     * @throws IOException
     */
    @Test
    public void testResponseBody() throws IOException {
        networkEventReporter = mock(NetworkEventReporter.class);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor(RuntimeEnvironment.application);
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

    @Test
    public void testInterceptedResponseWithContentLength() throws IOException {
        networkEventReporter = mock(NetworkEventReporter.class);

        NetworkInterceptor networkInterceptor = new NetworkInterceptor(RuntimeEnvironment.application);
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
                .header("Content-Length", String.valueOf(8))
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(ResponseBody.create(MediaType.parse("text/plain"), "any text"))
                .build();

        CustomChain customChain = new CustomChain(request, response, null);
        networkInterceptor.intercept(customChain);

        //intercepted request object
        Response interceptedResponse = customChain.proceed(request);
        System.out.println(interceptedResponse.body().contentLength());
        //assert response code
        Assert.assertTrue(interceptedResponse.body().contentLength() == 8);
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
