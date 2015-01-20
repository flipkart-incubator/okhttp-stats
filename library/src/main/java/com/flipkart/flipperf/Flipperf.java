package com.flipkart.flipperf;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.flipkart.fk_android_batchnetworking.BatchNetworking;
import com.flipkart.fk_android_batchnetworking.JSONDataHandler;
import com.flipkart.flipperf.models.PerfContext;
import com.flipkart.flipperf.models.PerfDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;

public class Flipperf {

	private static boolean LOGGING = true;
	private static boolean AUTO_PERFORMANCE_LOGGING = true;
	private static boolean AUTO_UI_PERFORMANCE_LOGGING = true;
	private static boolean AUTO_CONNECTION_PERFORMANCE_LOGGING = true;
	private static boolean MONITOR_BATTERY = true;

	private static final String TAG = "Flipperf";
	private static final String PERFORMANCE_EVENTS = "flipperf";

	public enum TagState {
		START("start"), INTERMEDIATE("intermediate"), END("end");
		private String name;

		private TagState(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private String perfPushURL = null;

	public static Flipperf getInstance() {
		return instance;
	}

	public static void track(FlipperfTag tag, TagState tagState, String info) {
		Flipperf.getInstance().track(tag, tagState.getName(), info, null);
	}

	public static void track(FlipperfTag tag, TagState tagState,
			JsonElement info) {
		Flipperf.track(tag, tagState.getName(), info);
	}

	public static void track(FlipperfTag tag, String tagState, JsonElement info) {
		Flipperf.getInstance().track(tag, tagState, info, null);
	}

	private HashMap<Object, Number> dateMarkers = null;
	public Context applicationContext = null;

	public Context getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(Context applicationContext) {
		this.applicationContext = applicationContext;

		// read the default settings from the XML
		readSettings(applicationContext);

		// initialize Batchnetworking and other stuff for Flipperf
		initialize(applicationContext);
	}

	private void readSettings(Context applicationContext) {
		try {
			ApplicationInfo ai = applicationContext.getPackageManager()
					.getApplicationInfo(applicationContext.getPackageName(),
							PackageManager.GET_META_DATA);
			Bundle aBundle = ai.metaData;
			int resource = aBundle.getInt("com.flipkart.fk_android_flipperf");

			Log.d(TAG, "resource = " + resource);

			XmlPullParser parser = applicationContext.getResources().getXml(
					resource);
			Log.d(TAG, "parser = " + parser);

			int eventType = parser.getEventType();
			String name = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					name = parser.getName();
				} else if (eventType == XmlPullParser.TEXT) {
					if ((name != null) && name.equals("flipperf_url")) {
						perfPushURL = parser.getText();
					} else if ((name != null) && name.equals("flipperf_off")) {
						setLogging(!Boolean.parseBoolean(parser.getText()));
					}
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	void setLogging(boolean log) {
		LOGGING = log;
	}

	void setAutoPerformanceLogging(boolean log) {
		AUTO_PERFORMANCE_LOGGING = log;
	}

	public static boolean isAutoUIPerformanceLoggingON() {
		return (AUTO_UI_PERFORMANCE_LOGGING && AUTO_PERFORMANCE_LOGGING && LOGGING);
	}

	public static void setAutoUIPerformanceLogging(boolean log) {
		AUTO_UI_PERFORMANCE_LOGGING = log;
	}

	public static boolean isBatteryMonitoringON() {
		return (MONITOR_BATTERY && AUTO_PERFORMANCE_LOGGING && LOGGING);
	}

	public static void setBatteryMonitoring(boolean log) {
		MONITOR_BATTERY = log;
	}

	public static boolean isAutoConnectionPerformanceLoggingON() {
		return (AUTO_CONNECTION_PERFORMANCE_LOGGING && AUTO_PERFORMANCE_LOGGING && LOGGING);
	}

	public static void setAutoConnectionPerformanceLogging(boolean log) {
		AUTO_CONNECTION_PERFORMANCE_LOGGING = log;
	}

	private void initialize(Context applicationContext) {
		// Do lazy initialization of BatchNetworking as the application context
		// is not available when flipperf is initialized
		if (!isBatchNetworkingInitialized && applicationContext != null
				&& perfPushURL != null) {
			isBatchNetworkingInitialized = true;
			BatchNetworking.getDefaultInstance().initialize(
					getApplicationContext());
			try {
				BatchNetworking.getDefaultInstance().setGroupDataHandler(
						new JSONDataHandler(PERFORMANCE_EVENTS, perfPushURL));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private PerfContext context = new PerfContext();
	private Gson gson = null;

	/**
	 * Instance creation
	 */
	public static final Flipperf instance = new Flipperf();
	private Handler trackerHandler;
	boolean isBatchNetworkingInitialized = false;

	private Flipperf() {
		dateMarkers = new HashMap<Object, Number>();
		resetGlobalContext("appLoad");
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();

		HandlerThread thread = new HandlerThread("TrackerHandler");
		thread.setPriority(Thread.NORM_PRIORITY - 2);
		thread.start();
		trackerHandler = new Handler(thread.getLooper());
	}

	public void track(FlipperfTag tag, String state, String info,
			Object uniqueKey) {
		JsonElement element = null;
		if (info != null) {
			try {
				element = new JsonParser().parse(info);
			} catch (Exception e) {
				// e.printStackTrace();
				try {
					Gson gson = new GsonBuilder().disableHtmlEscaping()
							.create();
					info = gson.toJson(info).toString();
					element = new JsonParser().parse(info);
				} catch (Exception ex) {
					e.printStackTrace();
					Log.e(TAG, "Error in parsing element " + ex);
				}
			}
		}
		track(tag, state, element, uniqueKey);
	}

	public void trackSilentSTARTState(final FlipperfTag tag,
			Object uniqueKeyInternal) {
		if (null == uniqueKeyInternal) {
			uniqueKeyInternal = tag.tagName;
		}
		Long startTime = Long.valueOf(System.nanoTime());
		dateMarkers.put(uniqueKeyInternal, startTime);
	}

	public void track(final FlipperfTag tag, final String state,
			final JsonElement info, final Object uniqueKey) {

		// Log.i(TAG, "In track, the actual function");

		// don't log if logging is off
		if (!LOGGING)
			return;

		trackerHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Object uniqueKeyInternal = uniqueKey;
					if (null == uniqueKeyInternal) {
						uniqueKeyInternal = tag.tagName;
					}

					// get tag data
					PerfDataSet element = new PerfDataSet();
					PerfDataSet.Header elementHeader = element.header;
					PerfDataSet.Data elementData = element.data;

					// populate header
					elementHeader.configName = tag.tagName;

					// set the data
					// set state
					elementData.state = state;

					elementData.context = context.copy();
					// set other data
					elementData.context.info = info;

					Long startTime = null;
					if (state.equals(TagState.START.getName())) {
						startTime = Long.valueOf(System.nanoTime());
						dateMarkers.put(uniqueKeyInternal, startTime);
					} else {
						startTime = (Long) dateMarkers.get(uniqueKeyInternal);
						if (startTime == null)
							startTime = Long.valueOf(System.nanoTime());
						if (state.equals(TagState.END.getName())) {
							dateMarkers.remove(uniqueKeyInternal);
							long lngStartTime = startTime.longValue();
							long endTime = System.nanoTime();

							// time taken to load
							double elapsedTime = ((double) endTime - (double) lngStartTime) / 1000000;
							elementData.endTime = Long.valueOf(endTime);
							elementData.loadTime = Double.valueOf(elapsedTime);
						}
					}
					elementData.startTime = startTime;

					BatchNetworking.getDefaultInstance().pushDataForGroupId(
							gson.toJsonTree(element), PERFORMANCE_EVENTS);

				} catch (Exception ex) {
					ex.printStackTrace();
					Log.i(TAG, "Exception in track " + ex);
				}
			}
		});
	}

	public void resetGlobalContext(String globalContext) {
		context.resetContext();
		context.contextGlobal = globalContext;
		context.contextGlobalId = System.currentTimeMillis();
	}

	public void resetLocalContext(String localContext) {
		context.contextLocal = localContext;

		if (localContext == null) {
			context.contextLocalId = null;
		} else {
			context.contextLocalId = System.currentTimeMillis();
		}
	}

}
