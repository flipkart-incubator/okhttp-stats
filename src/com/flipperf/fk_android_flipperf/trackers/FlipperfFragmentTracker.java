package com.flipperf.fk_android_flipperf.trackers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface FlipperfFragmentTracker {
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);
}
