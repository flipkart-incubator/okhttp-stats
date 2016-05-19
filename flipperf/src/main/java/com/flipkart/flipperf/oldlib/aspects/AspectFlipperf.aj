package com.flipkart.fk_android_flipperf.oldlib.aspects;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.flipkart.fk_android_flipperf.oldlib.Flipperf;
import com.flipkart.fk_android_flipperf.oldlib.FlipperfTag;
import com.flipkart.fk_android_flipperf.oldlib.Flipperf.TagState;
import com.flipperf.fk_android_flipperf.oldlib.trackers.FlipperfFragmentTracker;
import com.flipperf.fk_android_flipperf.oldlib.trackers.FlipperfRequestQueueHolder;
import com.flipperf.fk_android_flipperf.oldlib.trackers.FlipperfRequst;

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
		com.flipkart.fk_android_flipperf.Flipperf.getInstance()
				.setApplicationContext(applicationContext);
	}

	/**
	 * Activity oncreate
	 **/
	pointcut activityOnCreate() : if(Flipperf.isAutoUIPerformanceLoggingON()) && execution(* Activity+.onCreate(..));

	before() : activityOnCreate() {
		String className = thisJoinPoint.getThis().getClass().getSimpleName();
		Flipperf.getInstance().trackSilentSTARTState(null,
				"onCreate|" + className);
		// Log.d(ATAG, "Before activityOnCreate");
	}

	after() : activityOnCreate() {
		String className = thisJoinPoint.getThis().getClass().getSimpleName();
		Flipperf.track(
				FlipperfTag.internalTag.createChildTagWithName("onCreate|"
						+ className), TagState.END, (String) null);
		// Log.d(ATAG, "After activityOnCreate");
	}

	/**
	 * Fragment oncreate
	 **/
	pointcut fragmentOnCreateView() : if(Flipperf.isAutoUIPerformanceLoggingON()) && (execution(* FlipperfFragmentTracker+.onCreateView(..)) || execution(* Fragment+.onCreateView(..)));

	before() : fragmentOnCreateView() {
		String className = thisJoinPoint.getThis().getClass().getSimpleName();
		Flipperf.getInstance().trackSilentSTARTState(null,
				"FragCreateView|" + className);
	}

	after() : fragmentOnCreateView() {
		String className = thisJoinPoint.getThis().getClass().getSimpleName();
		Flipperf.track(FlipperfTag.internalTag
				.createChildTagWithName("FragCreateView|" + className),
				TagState.END, (String) null);
		Log.d(ATAG, "After logging fragmentOnCreateView");
	}

	/**
	 * Base adapter getView
	 **/
	pointcut baseAdapterGetView() : if(Flipperf.isAutoUIPerformanceLoggingON()) && execution(* BaseAdapter+.getView(..));

	before() : baseAdapterGetView() {
		String className = thisJoinPoint.getThis().getClass().getSimpleName();
		Flipperf.getInstance().trackSilentSTARTState(null,
				"BAGetView|" + className);
	}

	after() : baseAdapterGetView() {
		String className = thisJoinPoint.getThis().getClass().getSimpleName();
		Flipperf.track(
				FlipperfTag.internalTag.createChildTagWithName("BAGetView|"
						+ className), TagState.END, (String) null);
		// Log.d(ATAG, "After logging baseAdapterGetView");
	}

	/**
	 * Log the time from putting a request in a Volley RequestQueue till you get
	 * a call in parseNetworkResponse method of com.android.volley.Request
	 */
	pointcut addToConnectionRequestQueue() : if(Flipperf.isAutoConnectionPerformanceLoggingON()) && (execution(* FlipperfRequestQueueHolder+.addToVolley(..)));

	before() : addToConnectionRequestQueue() {
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.START.getName(), info, info);
		// Log.d(ATAG, "=========== connectionQueue " + info);
	}

	pointcut connectionResponse() : if(Flipperf.isAutoConnectionPerformanceLoggingON()) && (execution(* FlipperfRequst+.parseNetworkResponse(..)));

	after() : connectionResponse() {
		Request request = (Request) thisJoinPoint.getThis();
		String info = request.getUrl();
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.END.getName(), info, info);
		// Log.d(ATAG, "=========== connectionResponse " + info);
	}

	/**
	 * Log conn: Volley needs to be compiled with this pointcut
	 **/
	pointcut addToVolleyQueue() : if(Flipperf.isAutoConnectionPerformanceLoggingON()) && (execution(* RequestQueue+.add(..)));

	before() : addToVolleyQueue() {
		// Log.d(ATAG, "=========== addToVolleyQueue 0 ");
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.START.getName(), info, info);
	}

	pointcut volleyResponse() : if(Flipperf.isAutoConnectionPerformanceLoggingON()) && (execution(* com.android.volley.RequestQueue+.finish(..)));

	after() : volleyResponse() {
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		// Log.d(ATAG, "=========== volleyResponse 1 " + info);
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.END.getName(), info, info);
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
