package com.flipkart.sampleapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.flipkart.fkvolley.RequestQueue;
import com.flipkart.fkvolley.toolbox.OkHttpStack;
import com.flipkart.fkvolley.toolbox.Volley;
import com.flipkart.flipperf.FlipperfNetwork;
import com.flipkart.flipperf.models.NetworkType;
import com.flipkart.flipperf.trackers.FlipperfNetworkStatManager;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestRequest com = new TestRequest(0, "http://upload.wikimedia.org/wikipedia/commons/5/5b/Ultraviolet_image_of_the_Cygnus_Loop_Nebula_crop.jpg", new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyError e = error;
            }
        });
        RequestQueue req = Volley.newRequestQueue(this, new FlipperfNetwork(new OkHttpStack(), this), 1);
        req.add(com);

        TestRequest com1 = new TestRequest(0, "http://news.bbcimg.co.uk/media/images/71832000/jpg/_71832498_71825880.jpg", new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyError e = error;
            }



        });
        req.add(com1);
        NetworkType networkType  =FlipperfNetworkStatManager.getInstance(getApplicationContext()).getCurrentNetworkType();
        System.out.println("Nertwork Speed === " + networkType.getNetworkSpeed());
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

    @Override
    protected void onDestroy() {
        FlipperfNetworkStatManager.getInstance(getApplicationContext()).destroy();
        super.onDestroy();
    }
}
