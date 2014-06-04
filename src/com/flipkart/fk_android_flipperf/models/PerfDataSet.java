package com.flipkart.fk_android_flipperf.models;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flipkart.fk_android_flipperf.Flipperf;
import com.google.mygson.annotations.SerializedName;

public class PerfDataSet {

	private static final String TAG = "PerfDataSet";
	public static String appName = null;

	private static String getAppName() {
		if (appName == null) {
			final PackageManager pm = Flipperf.getInstance()
					.getApplicationContext().getPackageManager();
			ApplicationInfo ai;
			try {
				ai = pm.getApplicationInfo(Flipperf.getInstance()
						.getApplicationContext().getPackageName(), 0);
			} catch (final NameNotFoundException e) {
				ai = null;
			}
			appName = (String) (ai != null ? pm.getApplicationLabel(ai)
					: "(unknown)");
		}
		return appName;
	}

	public static String appUniqueId = null;

	private static String getAppUniqueId() {
		if (appUniqueId == null) {
			appUniqueId = Secure.getString(Flipperf.getInstance()
					.getApplicationContext().getContentResolver(),
					Secure.ANDROID_ID);
			if (appUniqueId == null)
				appUniqueId = "";
		}
		return appUniqueId;
	}

	private static DeviceInfo deviceInfo = null;

	@SerializedName("header")
	public Header header;

	@SerializedName("data")
	public Data data;

	public PerfDataSet() {
		if (deviceInfo == null) {
			Log.i(TAG, "Getting device info");
			deviceInfo = new DeviceInfo();
			deviceInfo.deviceId = Secure.getString(Flipperf.getInstance()
					.getApplicationContext().getContentResolver(),
					Secure.ANDROID_ID);

			Log.i(TAG, "Before getting the telephony manager");
			TelephonyManager tel = (TelephonyManager) Flipperf.getInstance()
					.getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			Log.i(TAG, "telephony manager = " + tel);
			String networkOperator = tel.getNetworkOperator();
			if (networkOperator != null) {
				deviceInfo.mcc = networkOperator.substring(0, 3);
				deviceInfo.mnc = networkOperator.substring(3);
			}
		}
		Log.i(TAG, "Before header");
		header = new Header();
		Log.i(TAG, "Before data");
		data = new Data();
		Log.i(TAG, "After data");
		data.deviceInfo = deviceInfo;
	}

	private Number getBatteryLevel() {
		// TODO implement the method
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = Flipperf.getInstance().getApplicationContext()
				.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float) scale;

		return Float.valueOf(batteryPct);
	}

	private Number getCPULevel() {
		float cpuLevel = 0.0f;
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" ");

			long idle1 = Long.parseLong(toks[4]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[5]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" ");

			long idle2 = Long.parseLong(toks[4]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[5]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			cpuLevel = (float) (cpu2 - cpu1)
					/ ((cpu2 + idle2) - (cpu1 + idle1));

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return Float.valueOf(cpuLevel);
	}

	public class Data {

		@SerializedName("startTime")
		public Long startTime;

		@SerializedName("loadTime")
		public Long loadTime;

		@SerializedName("endTime")
		public Long endTime;

		@SerializedName("state")
		public String state;

		@SerializedName("context")
		public PerfContext context;

		@SerializedName("appName")
		public String appName = getAppName();

		@SerializedName("cpu")
		public Number cpu = getCPULevel();

		@SerializedName("batteryLevel")
		public Number batteryLevel = getBatteryLevel();

		@SerializedName("deviceInfo")
		public DeviceInfo deviceInfo;
	}

	class DeviceInfo {
		@SerializedName("cn")
		public float cn;

		@SerializedName("mnc")
		public String mnc;

		@SerializedName("mcc")
		public String mcc;

		@SerializedName("deviceId")
		public String deviceId;
	}

	public class Header {
		@SerializedName("instanceId")
		public String instanceId = getAppUniqueId();

		@SerializedName("eventId")
		public Long eventId = Long.valueOf(System.nanoTime());

		@SerializedName("configName")
		public String configName;

		@SerializedName("profile")
		public String profile = "android";

		@SerializedName("appName")
		public String appName = "flipperf";

		@SerializedName("timestamp")
		public Long timestamp = Long.valueOf(System.currentTimeMillis());
	}
}