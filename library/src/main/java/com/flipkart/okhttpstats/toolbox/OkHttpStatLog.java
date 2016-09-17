package com.flipkart.okhttpstats.toolbox;

public final class OkHttpStatLog {
    private static boolean isLoggingEnabled = true;

    public static boolean isLoggingEnabled() {
        return isLoggingEnabled;
    }

    public static void enableLogging(boolean isLoggingEnabled) {
        OkHttpStatLog.isLoggingEnabled = isLoggingEnabled;
    }
}