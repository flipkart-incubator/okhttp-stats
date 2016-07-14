package com.flipkart.flipperfdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Network;
import com.android.volley.toolbox.BasicNetwork;
import com.flipkart.fkvolley.RequestQueue;
import com.flipkart.fkvolley.toolbox.ImageLoader;
import com.flipkart.fkvolley.toolbox.OkHttp3Stack;
import com.flipkart.okhttpstats.NetworkInterceptor;
import com.flipkart.okhttpstats.handler.OnResponseListener;
import com.flipkart.okhttpstats.handler.PersistentStatsHandler;
import com.flipkart.okhttpstats.interpreter.DefaultInterpreter;
import com.flipkart.okhttpstats.interpreter.NetworkInterpreter;
import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.reporter.NetworkEventReporterImpl;

import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private OnResponse onResponseReceived;
    private PersistentStatsHandler networkRequestStatsHandler;
    private boolean isNewFlipperf = true;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (isNewFlipperf) {
            onResponseReceived = new OnResponse();
            networkRequestStatsHandler = new PersistentStatsHandler(this);
            networkRequestStatsHandler.addListener(onResponseReceived);
            NetworkInterpreter networkInterpreter = new DefaultInterpreter(new NetworkEventReporterImpl(networkRequestStatsHandler));

            NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
                    .setNetworkInterpreter(networkInterpreter)
                    .setEnabled(true)
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(networkInterceptor)
                    .build();
            OkHttp3Stack okHttp3Stack = new OkHttp3Stack(okHttpClient);
            imageLoader = new ImageLoader(initializeVolleyQueue(this, new BasicNetwork(okHttp3Stack)), new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
                        10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            }, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
                        10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }

        final com.flipkart.fkvolley.toolbox.NetworkImageView networkImageView = (com.flipkart.fkvolley.toolbox.NetworkImageView) findViewById(R.id.img);
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

    private RequestQueue initializeVolleyQueue(Context context, Network network) {
        return com.flipkart.fkvolley.toolbox.Volley.newRequestQueue(context, network, 2);
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

    private class OnResponse implements OnResponseListener {

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
        public void onResponseError(NetworkInfo info, RequestStats requestStats, Exception e) {
            Log.d(MainActivity.class.getName(), "onResponseErrorReceived : "
                    + "\nId : " + requestStats.getId()
                    + "\nUrl : " + requestStats.getUrl()
                    + "\nMethod : " + requestStats.getMethodType()
                    + "\nHost : " + requestStats.getHostName()
                    + "\nRequest Size : " + requestStats.getRequestSize()
                    + "\nResponse Size : " + requestStats.getResponseSize()
                    + "\nTime Taken: " + (requestStats.getEndTime() - requestStats.getStartTime())
                    + "\nStatus Code : " + requestStats.getStatusCode()
                    + "\nException : " + e.getMessage());
        }
    }
}
