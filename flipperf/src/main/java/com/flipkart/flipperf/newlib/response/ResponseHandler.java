package com.flipkart.flipperf.newlib.response;

/**
 * Implementations are to read the number of bytes in case the response header do not have content-length
 */
public interface ResponseHandler {

    void onRead(int numBytes);

    /**
     * Signals that EOF has been reached reading the response stream from the network
     * stack.
     */
    void onEOF();
}

