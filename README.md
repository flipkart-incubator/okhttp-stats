#okhttp-Stats

OkHttp-Stats is an android library built on top of OkHttp, which is responsible for intercepting all the network calls and for calculating network stats such as the average network speed of the user. This is more of a analytical tool which can be used to track the success and error responses.

###How to use ?

````java
        
        OnResponseReceived onResponseReceived = new OnResponseReceived();
        PersistentStatsHandler networkRequestStatsHandler = new PersistentStatsHandler(this);
        networkRequestStatsHandler.addListener(onResponseReceived);
        
        NetworkInterpreter networkInterpreter = new DefaultInterpreter(new NetworkEventReporterImpl(networkRequestStatsHandler));

        NetworkInterceptor networkInterceptor = new NetworkInterceptor.Builder()
              .setNetworkInterpreter(networkInterpreter)
              .setEnabled(true)
              .build();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
              .addInterceptor(networkInterceptor)
              .build();

````
Now, pass this okHttpClient (which has an interceptor added) to the okhttpstack, and you are done.

###Getting Started 

Gradle Dependency : ````compile 'com.flipkart.android.okhttpstats:library:1.0.0'````

###[Wiki](https://github.com/Flipkart/okhttp-stats/wiki)

###Dependencies

* [OkHttp](https://github.com/square/okhttp)
* [Slf4J](http://www.slf4j.org/)
* [JUnit](http://junit.org/), [Roboelectric](http://robolectric.org/), [Mockito](http://mockito.org/)
