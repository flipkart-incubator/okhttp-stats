package com.flipkart.flipperf;

import android.content.Context;
import android.util.Log;

import com.flipkart.flipperf.trackers.Event;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by nikhil.n on 15/01/15.
 */
public class FlipperfEventManager<T> {

    private static FlipperfEventManager flipperfEventManager;
    private final String TAG = FlipperfEventManager.class.getName();
    private Map<T, Event> eventMap;
    private Context context;

    private FlipperfEventManager(Context context) {
        eventMap = new HashMap<T, Event>();
        this.context = context;
    }

    public static synchronized FlipperfEventManager getInstance(Context context) {
        if (flipperfEventManager == null)
            flipperfEventManager = new FlipperfEventManager(context);
        return flipperfEventManager;
    }

    public synchronized void startEvent(Event<T> event) {
        Log.d(TAG, "start event " + event.getEventId());
        eventMap.put(event.getEventId(), event);
        event.onEventStarted();
    }

    public synchronized void removeEvent(T eventId) {
        Log.d(TAG, "remove event " + eventId);
        if (eventMap.containsKey(eventId)) {
            eventMap.remove(eventId);
        }
    }

    public synchronized void stopEvent(T eventId) {
        Log.d(TAG, "stop event " + eventId);
        if (eventMap.containsKey(eventId)) {
            Event event = eventMap.get(eventId);
            eventMap.remove(eventId);
            event.onEventFinished();
        }
    }
}
