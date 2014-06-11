package com.flipkart.fk_android_flipperf;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.flipkart.fk_android_batchnetworking.BatchNetworking;
import com.flipkart.fk_android_batchnetworking.JSONDataHandler;
import com.flipkart.fk_android_flipperf.models.PerfContext;
import com.flipkart.fk_android_flipperf.models.PerfDataSet;
import com.google.mygson.Gson;
import com.google.mygson.GsonBuilder;
import com.google.mygson.JsonElement;
import com.google.mygson.JsonParser;

public class Flipperf {

	public static final boolean AUTO_PERFORMANCE_MAPPING = true;
	public static final boolean AUTO_CONNECTION_PERFORMANCE_MAPPING = true;
	public static final boolean AUTO_UI_PERFORMANCE_MAPPING = true;

	private static final String TAG = "Flipperf";
	private static final String PERFORMANCE_EVENTS = "perf";

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

	public static boolean MONITOR_BATTERY = true;
	public static boolean UI_PERFORMANCE_LOGGING = true;
	private static final String BASE_URL_STRING = "http://stage-hyperion-api.digital.ch.flipkart.com:8201/apps/configs/events/bulk";

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

		// Do lazy initialization of BatchNetworking as the application context
		// is not available when flipperf is initialized
		if (!isBatchNetworkingInitialized) {
			isBatchNetworkingInitialized = true;
			BatchNetworking.getDefaultInstance().initialize(
					getApplicationContext());
			try {
				BatchNetworking.getDefaultInstance()
						.setGroupDataHandler(
								new JSONDataHandler(PERFORMANCE_EVENTS,
										BASE_URL_STRING));
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
							double elapsedTime = ((double)endTime - (double)lngStartTime)/1000000;
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
