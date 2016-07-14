#okhttp-stats

OkHttp-Stats is an android library built on top of OkHttp3, which is responsible for intercepting all the network calls and for calculating network stats such as the average network speed of the user. This is more of an analytical tool which can be used to track the success and error responses.

Can be plugged in to any app which uses okhttp in their networking stack.

###How to use ?

Create a class that implements the ````OnResponseListener````. This is where you will get all the callbacks in case of success or error responses.

````java
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
````

Initialize the NetworkInterceptor, and register your listener.

````java
        
        //listener
        OnResponseReceived onResponseReceived = new OnResponseReceived();
        
        PersistentStatsHandler networkRequestStatsHandler = new PersistentStatsHandler(this);
        //register your listener with the PersistentStatsHandler
        networkRequestStatsHandler.addListener(onResponseReceived);
        
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(new NetworkEventReporterImpl(networkRequestStatsHandler));

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
              .setNetworkInterpreter(networkInterpreter)
              .setEnabled(true)
              .build();
        
        //add the networkinterceptor to the okhttpclient
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
              .addInterceptor(networkInterceptor)
              .build();

````
Now, pass the okHttpClient (which has an interceptor added) to the okhttpstack, and you are done.

###[Wiki](https://github.com/Flipkart/okhttp-stats/wiki)

###Dependencies

* [OkHttp](https://github.com/square/okhttp)
* [Slf4J](http://www.slf4j.org/)
* [JUnit](http://junit.org/), [Roboelectric](http://robolectric.org/), [Mockito](http://mockito.org/)
