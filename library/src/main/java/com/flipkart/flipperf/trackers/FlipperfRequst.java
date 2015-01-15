package com.flipkart.flipperf.trackers;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

public interface FlipperfRequst<T> {

	abstract Response<T> parseNetworkResponse(NetworkResponse response);

}