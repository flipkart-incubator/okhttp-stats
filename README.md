#okhttp-stats [![](https://jitpack.io/v/flipkart-incubator/okhttp-stats.svg)](https://jitpack.io/#flipkart-incubator/okhttp-stats)

OkHttp-Stats is an android library built on top of OkHttp3, which is responsible for intercepting all the network calls and for calculating network stats such as the average network speed of the user. This is more of an analytical tool which can be used to track the success and error responses.

Can be plugged in to any app which uses okhttp in their networking stack.

###Get okhttp-stats

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}

Add the dependency:

	dependencies {
	        compile 'com.github.flipkart-incubator:okhttp-stats:1.0'
	}


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

###Licence

```
The MIT License (MIT)

Copyright (c) 2016 Flipkart Internet Pvt. Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
