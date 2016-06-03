package com.flipkart.flipperf.trackers;

/**
 * Created by nikhil.n on 15/01/15.
 */
public interface Event<T> {

    public void onEventStarted();

    public void onEventFinished();

    public T getEventId();

    public void setEventId(T eventId);

}
