package com.flipkart.flipperf.response;

import com.flipkart.flipperf.NetworkEventReporter;

public interface ResponseHandler {
    /**
     * Signal that data has been read from the response stream.
     *
     * @param numBytes Bytes read from the network stack's stream as established by
     *                 {@link NetworkEventReporter#interpretResponseStream}.
     */
    void onRead(int numBytes);

    /**
     * Signals that EOF has been reached reading the response stream from the network
     * stack.
     */
    void onEOF();
}

