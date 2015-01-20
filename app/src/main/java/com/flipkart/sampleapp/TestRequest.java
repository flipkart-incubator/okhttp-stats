package com.flipkart.sampleapp;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.flipkart.fkvolley.Request;

/**
 * Created by nikhil.n on 16/01/15.
 */
public class TestRequest extends Request<String> {

    private String requestId = null;


    public TestRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        setRetryPolicy(new DefaultRetryPolicy());
    }

    public TestRequest(String url, Response.ErrorListener listener) {
        super(url, listener);
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        NetworkResponse a = response;
        return null;
    }

    @Override
    protected void deliverResponse(String response) {
        System.out.println(response);
    }
}
