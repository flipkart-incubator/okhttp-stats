/*
 *
 *  * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.flipkart.okhttpstatsdemo;

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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.circle.android.api.OkHttp3Stack;
import com.flipkart.okhttpstats.NetworkInterceptor;
import com.flipkart.okhttpstats.handler.OnResponseListener;
import com.flipkart.okhttpstats.handler.PersistentStatsHandler;
import com.flipkart.okhttpstats.interpreter.DefaultInterpreter;
import com.flipkart.okhttpstats.interpreter.NetworkInterpreter;
import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.reporter.NetworkEventReporterImpl;

import java.util.Random;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private OnResponseReceived onResponseReceived;
    private PersistentStatsHandler networkRequestStatsHandler;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onResponseReceived = new OnResponseReceived();
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
        new BasicNetwork(okHttp3Stack);

        RequestQueue requestQueue = Volley.newRequestQueue(this, okHttp3Stack);

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
        });

        final NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.img);
        assert networkImageView != null;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rand = new Random().nextInt(ResourceList.URLS.length);
                String url = ResourceList.URLS[rand];
                networkImageView.setImageUrl(url, imageLoader);
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

    private class OnResponseReceived implements OnResponseListener {

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
