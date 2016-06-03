package com.flipkart.flipperf.newlib.toolbox;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by anirudh.r on 03/06/16 at 2:52 PM.
 */
public class Utils {
    private static final String CONTENT_LENGTH = "Content-Length";

    public static long getContentLength(Request request) {
        String header = getContentLengthHeader(request);
        if (request != null && header != null) {
            try {
                long l = Long.parseLong(header);
                if (l > 0) return l;
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    public static String getContentLengthHeader(Request request) {
        return request.header(CONTENT_LENGTH);
    }

    public static long getContentLength(Response response) {
        String header = getContentLengthHeader(response);
        if (response != null && header != null) {
            try {
                long l = Long.parseLong(header);
                if (l > 0) return l;
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    public static String getContentLengthHeader(Response response) {
        if (response != null) {
            return response.header(CONTENT_LENGTH);
        } else {
            return null;
        }
    }

    public static long stringToLong(String s) {
        if (s == null) return 0;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
