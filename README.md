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

###License

````
The MIT License (MIT)
Copyright (c) <year> <copyright holders>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
````
