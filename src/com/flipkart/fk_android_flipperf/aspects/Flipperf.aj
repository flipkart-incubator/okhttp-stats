package com.flipkart.fk_android_flipperf.aspects;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.HurlStack;
import com.flipkart.fk_android_flipperf.Flipperf.TagState;
import com.flipkart.fk_android_flipperf.FlipperfTag;

//import com.android.volley.toolbox.HurlStack;

public aspect Flipperf {

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
		Log.d(ATAG, "=========== Hello Mudit 1" + method);
	}

	/**
	 * Log conn
	 **/
	pointcut performRequest() : (execution(* HurlStack+.performRequest(Request<?>, ..)));

	before() : performRequest() {
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		Log.d(ATAG, "=========== httpClient before 1 " + info);
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.START.getName(), info, info);
		Log.d(ATAG, "=========== httpClient before 2 " + info);
	}

	after() : performRequest() {
		Request request = (Request) thisJoinPoint.getArgs()[0];
		String info = request.getUrl();
		Log.d(ATAG, "=========== httpClient after 1 " + info);
		com.flipkart.fk_android_flipperf.Flipperf.getInstance().track(
				FlipperfTag.connTag, TagState.END.getName(), info, info);
		Log.d(ATAG, "=========== httpClient after 2 " + info);
	}
}
