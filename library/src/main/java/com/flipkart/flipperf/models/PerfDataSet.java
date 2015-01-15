package com.flipkart.flipperf.models;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.flipkart.fk_android_batchnetworking.Connectivity;
import com.flipkart.flipperf.Flipperf;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

			appUniqueId = md5(appUniqueId);
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
			deviceInfo = new DeviceInfo();
			deviceInfo.deviceId = getAppUniqueId();

			TelephonyManager tel = (TelephonyManager) Flipperf.getInstance()
					.getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tel != null) {
				String networkOperator = tel.getNetworkOperator();
				if (networkOperator != null) {
					deviceInfo.mcc = networkOperator.substring(0, 3);
					deviceInfo.mnc = networkOperator.substring(3);
				}
				deviceInfo.cn = tel.getNetworkOperatorName();

				deviceInfo.brand = Build.BRAND;
				deviceInfo.model = Build.MODEL;
				deviceInfo.version = Integer.valueOf(Build.VERSION.SDK_INT);
			}
		}
		header = new Header();
		data = new Data();
		data.deviceInfo = deviceInfo;
	}

	private Number getBatteryLevel() {
		if (!Flipperf.isBatteryMonitoringON())
			return -1;
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
		public Double loadTime;

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

		@SerializedName("network")
		public String network = Connectivity.getConnection(Flipperf
				.getInstance().getApplicationContext());

		@SerializedName("networkSpeed")
		public String networkSpeed = Connectivity.getConnectionSpeed(Flipperf
				.getInstance().getApplicationContext());

		@SerializedName("deviceInfo")
		public DeviceInfo deviceInfo;
	}

	class DeviceInfo {
		@SerializedName("cn")
		public String cn;

		@SerializedName("mnc")
		public String mnc;

		@SerializedName("mcc")
		public String mcc;

		@SerializedName("deviceId")
		public String deviceId;

		@SerializedName("brand")
		public String brand;

		@SerializedName("model")
		public String model;

		@SerializedName("version")
		public Integer version;
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

	public static final String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
