package com.flipkart.flipperf.newlib.toolbox;

import java.io.IOException;
import java.net.HttpRetryException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

public class ExceptionType {

    public final static int SOCKET_EXCEPTION = 601;
    public final static int PROTOCOL_EXCEPTION = 602;
    public final static int UNKNOWN_HOST_EXCEPTION = 603;
    public final static int UNKNOWN_SERVICE_EXCEPTION = 604;
    public final static int SOCKET_TIMEOUT_EXCEPTION = 605;
    public final static int MALFORMED_URL_EXCEPTION = 606;
    public final static int HTTP_RETRY_EXCEPTION = 606;
    public final static int UNKNOWN_EXCEPTION = -1;

    public static int getExceptionType(IOException e) {
        if (e != null) {
            if (e instanceof SocketException) {
                return SOCKET_EXCEPTION;
            } else if (e instanceof ProtocolException) {
                return PROTOCOL_EXCEPTION;
            } else if (e instanceof UnknownHostException) {
                return UNKNOWN_HOST_EXCEPTION;
            } else if (e instanceof UnknownServiceException) {
                return UNKNOWN_SERVICE_EXCEPTION;
            } else if (e instanceof SocketTimeoutException) {
                return SOCKET_TIMEOUT_EXCEPTION;
            } else if (e instanceof MalformedURLException) {
                return MALFORMED_URL_EXCEPTION;
            } else if (e instanceof HttpRetryException) {
                return HTTP_RETRY_EXCEPTION;
            } else {
                return UNKNOWN_EXCEPTION;
            }
        } else {
            return UNKNOWN_EXCEPTION;
        }
    }
}