/*
 * The MIT License
 *
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.flipkart.okhttpstatsdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.circle.android.api.OkHttp3Stack;
import com.flipkart.okhttpstats.kotlin.NetworkInterceptor;
import com.flipkart.okhttpstats.handler.PersistentStatsHandler;
import com.flipkart.okhttpstats.interpreter.DefaultInterpreter;
import com.flipkart.okhttpstats.kotlin.interpreter.NetworkInterpreter;
import com.flipkart.okhttpstats.reporter.NetworkEventReporterImpl;

import java.util.Random;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private OnResponseReceived onResponseReceived;
    private PersistentStatsHandler networkRequestStatsHandler;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private OkHttp3Stack okHttp3Stack;

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

        okHttp3Stack = new OkHttp3Stack(okHttpClient);

        final NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.img);
        assert networkImageView != null;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rand = new Random().nextInt(ResourceList.URLS.length);
                String url = ResourceList.URLS[rand];
                networkImageView.setImageUrl(url, getImageLoader());
                Snackbar.make(view, "Loading Image...", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }

    private RequestQueue getRequestQueue(Context context, HttpStack httpStack) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context, httpStack);
        }
        return requestQueue;
    }

    private ImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(getRequestQueue(this, okHttp3Stack), new ImageLoader.ImageCache() {

                @Override
                public Bitmap getBitmap(String url) {
                    return null;
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                }
            });
        }
        return imageLoader;
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
}
