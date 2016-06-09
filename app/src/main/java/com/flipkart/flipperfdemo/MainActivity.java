package com.flipkart.flipperfdemo;

import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.flipkart.fkvolley.toolbox.OkHttpStack2;
import com.flipkart.flipperf.newlib.NetworkInterceptor;
import com.flipkart.flipperf.newlib.handler.OnResponseReceivedListener;
import com.flipkart.flipperf.newlib.handler.PersistentStatsHandler;
import com.flipkart.flipperf.newlib.interpreter.DefaultInterpreter;
import com.flipkart.flipperf.newlib.interpreter.NetworkInterpreter;
import com.flipkart.flipperf.newlib.model.RequestStats;
import com.flipkart.flipperf.newlib.reporter.NetworkEventReporterImpl;
import com.flipkart.flipperf.newlib.toolbox.ExceptionType;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private OnResponseReceived onResponseReceived;
    private PersistentStatsHandler networkRequestStatsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        onResponseReceived = new OnResponseReceived();
        networkRequestStatsHandler = new PersistentStatsHandler(this);
        networkRequestStatsHandler.addListener(onResponseReceived);
        networkRequestStatsHandler.setMaxSizeForPersistence(10);
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(new NetworkEventReporterImpl(networkRequestStatsHandler));

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                .setEnabled(true)
                .setNetworkInterpreter(networkInterpreter)
                .build(this);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(networkInterceptor);
        OkHttpStack2 okHttpStack2 = new OkHttpStack2(okHttpClient);

        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        final ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(this, okHttpStack2, -1), imageCache);


        final NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.img);
        assert networkImageView != null;

        final ArrayList<String> image_list = ResourceList.getResourceList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rand = new Random().nextInt(image_list.size());
                networkImageView.setImageUrl(image_list.get(rand), imageLoader);
                Snackbar.make(view, "Loading Image...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        networkRequestStatsHandler.removeListener(onResponseReceived);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class BitmapLruCache
            extends LruCache<String, Bitmap>
            implements ImageLoader.ImageCache {

        public BitmapLruCache() {
            this(getDefaultLruCacheSize());
        }

        public BitmapLruCache(int sizeInKiloBytes) {
            super(sizeInKiloBytes);
        }

        public static int getDefaultLruCacheSize() {
            final int maxMemory =
                    (int) (Runtime.getRuntime().maxMemory() / 1024);
            return maxMemory / 8;
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }
    }

    private class OnResponseReceived implements OnResponseReceivedListener {

        @Override
        public void onResponseSuccess(NetworkInfo info, RequestStats requestStats) {
            Log.d(MainActivity.class.getName(), "onResponseSuccessReceived : "
                    + "\nId : " + requestStats.getId()
                    + "\nUrl : " + requestStats.getUrl()
                    + "\nMethod : " + requestStats.getMethodType()
                    + "\nHost : " + requestStats.getHostName()
                    + "\nRequest Size : " + requestStats.getRequestSize()
                    + "\nResponse Size : " + requestStats.getResponseSize()
                    + "\nTime Taken: " + (requestStats.getEndTime() - requestStats.getStartTime())
                    + "\nStatus Code : " + requestStats.getStatusCode());
        }

        @Override
        public void onResponseError(NetworkInfo info, RequestStats requestStats, IOException e) {
            Log.d(MainActivity.class.getName(), "onResponseErrorReceived : "
                    + "\nId : " + requestStats.getId()
                    + "\nUrl : " + requestStats.getUrl()
                    + "\nMethod : " + requestStats.getMethodType()
                    + "\nHost : " + requestStats.getHostName()
                    + "\nRequest Size : " + requestStats.getRequestSize()
                    + "\nResponse Size : " + requestStats.getResponseSize()
                    + "\nTime Taken: " + (requestStats.getEndTime() - requestStats.getStartTime())
                    + "\nStatus Code : " + requestStats.getStatusCode()
                    + "\nException Type : " + ExceptionType.getExceptionType(e)
                    + "\nException : " + e.getMessage());
        }
    }
}
