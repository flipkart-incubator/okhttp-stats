package com.flipkart.flipperf.newlib.response;

public interface ResponseHandler {

    void onRead(int numBytes);

    /**
     * Signals that EOF has been reached reading the response stream from the network
     * stack.
     */
    void onEOF();
}

