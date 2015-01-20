package com.flipkart.flipperf.trackers;

import com.android.volley.Request;

public interface FlipperfRequestQueueHolder {
	public void addToVolley(Request request);
}
