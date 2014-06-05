package com.flipkart.fk_android_flipperf.aspects;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.flipkart.fk_android_flipperf.Flipperf;
import com.flipkart.fk_android_flipperf.FlipperfTag;
import com.flipkart.fk_android_flipperf.Flipperf.TagState;
import com.flipperf.fk_android_flipperf.trackers.FlipperfRequestQueueHolder;
import com.flipperf.fk_android_flipperf.trackers.FlipperfRequst;

//import com.android.volley.toolbox.HurlStack;

public aspect AspectFlipperf {

	public boolean firstUI = true;
	public String ATAG = "LogAspect";
	public String ATAGC = "Conn";

	/** Log App init **/
	pointcut applicationONCreate() : execution(* Application.onCreate(..));

	after(): applicationONCreate() {
		Context applicationContext = ((Application) thisJoinPoint.getThis())
				.getApplicationContext();
		Log.d(ATAG, "Setting application context");
		com.flipkart.fk_android_flipperf.Flipperf.getInstance()
				.setApplicationContext(applicationContext);
	}

	/**
	 * Activity oncreate
	 **/
	pointcut activityMethods() : execution(* Activity+.onCreate(..));

	before() : activityMethods() {
		String method = thisJoinPoint.getSignature().toShortString();
		//
		// Log.d(ATAG, "=========== entering " +
		// method+", parms="+Arrays.toString(thisJoinPoint.getArgs()));
		// Log.d(ATAG, "=========== Hello Mudit 0" +
		// thisJoinPoint.getThis().getClass().getName());
		Log.d(ATAG, "=========== Hello Mudit 2" + method);
	}

	/**
	 * Log the time from putting a request in a Volley RequestQueue till you get
	 * a call in parseNetworkResponse method of com.android.volley.Request
	 */
	pointcut addToConnectionRequestQueue() : if(Flipperf.AUTO_CONNECTION_PERFORMANCE_MAPPING) && (execution(* FlipperfRequestQueueHolder+.addToVolley(..)));

	before() : addToConnectionRequestQueue() {
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.START.getName(), info, info);
		Log.d(ATAG, "=========== connectionQueue " + info);
	}

	pointcut connectionResponse() : if(Flipperf.AUTO_CONNECTION_PERFORMANCE_MAPPING) && (execution(* FlipperfRequst+.parseNetworkResponse(..)));

	after() : connectionResponse() {
		Request request = (Request) thisJoinPoint.getThis();
		String info = request.getUrl();
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.END.getName(), info, info);
		Log.d(ATAG, "=========== connectionResponse " + info);
	}

	/**
	 * Log conn: Volley needs to be compiled with this pointcut
	 **/
	pointcut addToVolleyQueue() : if(Flipperf.AUTO_CONNECTION_PERFORMANCE_MAPPING) && (execution(* RequestQueue+.add(..)));

	before() : addToVolleyQueue() {
		Log.d(ATAG, "=========== addToVolleyQueue 0 ");
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		Log.d(ATAG, "=========== addToVolleyQueue 1 " + info);
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.START.getName(), info, info);
		Log.d(ATAG, "=========== addToVolleyQueue 2 " + info);
	}

	pointcut volleyResponse() : if(Flipperf.AUTO_CONNECTION_PERFORMANCE_MAPPING) && (execution(* com.android.volley.RequestQueue+.finish(..)));

	after() : volleyResponse() {
		Log.d(ATAG, "=========== volleyResponse 0 ");
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		Log.d(ATAG, "=========== volleyResponse 1 " + info);
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.END.getName(), info, info);
		Log.d(ATAG, "=========== volleyResponse 2 " + info);
	}
	// pointcut performRequest() : (execution(*
	// HurlStack+.performRequest(Request<?>, ..)));
	//
	// before() : performRequest() {
	// Request request = (Request) thisJoinPoint.getArgs()[0];
	// String info = request.getUrl();
	// Log.d(ATAG, "=========== httpClient before 1 " + info);
	// com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
	// FlipperfTag.connTag, TagState.START.getName(), info, info);
	// Log.d(ATAG, "=========== httpClient before 2 " + info);
	// }
	//
	// after() : performRequest() {
	// Request request = (Request) thisJoinPoint.getArgs()[0];
	// String info = request.getUrl();
	// Log.d(ATAG, "=========== httpClient after 1 " + info);
	// com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
	// FlipperfTag.connTag, TagState.END.getName(), info, info);
	// Log.d(ATAG, "=========== httpClient after 2 " + info);
	// }
}
