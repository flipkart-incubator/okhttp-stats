package com.flipkart.flipperf;

import android.util.Log;

import com.flipkart.flipperf.trackers.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikhil.n on 15/01/15.
 */
public class FlipperfEventManager<T> {

    private final String TAG = FlipperfEventManager.class.getName();
    private Map<T, Event> eventMap;
    private static FlipperfEventManager flipperfEventManager;

    private FlipperfEventManager() {
        eventMap = new HashMap<T, Event>();
    }

    public static synchronized FlipperfEventManager getInstance() {
        if(flipperfEventManager == null)
            flipperfEventManager = new FlipperfEventManager();
        return flipperfEventManager;
    }

    public synchronized void startEvent(Event<T> event) {
        Log.d(TAG, "start event "+event.getEventId());
        eventMap.put(event.getEventId(), event);
        event.onEventStarted();
    }

    public synchronized void removeEvent(T eventId) {
        Log.d(TAG, "remove event "+eventId);
        if(eventMap.containsKey(eventId)) {
            eventMap.remove(eventId);
        }
    }

    public synchronized void stopEvent(T eventId) {
        Log.d(TAG, "stop event "+eventId);
        if(eventMap.containsKey(eventId)) {
            Event event = eventMap.get(eventId);
            eventMap.remove(eventId);
            event.onEventFinished();
        }
    }

}
