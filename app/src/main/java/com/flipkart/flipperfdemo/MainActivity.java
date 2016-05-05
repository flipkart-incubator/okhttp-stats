package com.flipkart.flipperfdemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.flipkart.fkvolley.RequestQueue;
import com.flipkart.fkvolley.toolbox.OkHttp2Stack;
import com.flipkart.fkvolley.toolbox.Volley;
import com.flipkart.flipperf.NetworkInterceptor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TestRequest com = new TestRequest(0, "http://rukmini1.flixcart.com/image/225/225/shoe/b/t/y/rosso-corsa-white-vibrant-yellow-30461901-puma-1-original-imadwdftjmwyuxkt.jpeg?q=90", new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyError e = error;
            }
        });

        final RequestQueue req = Volley.newRequestQueue(this, new OkHttp2Stack(), 1);
        OkHttp2Stack.setInterceptor(new NetworkInterceptor(this));
        req.add(com);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
