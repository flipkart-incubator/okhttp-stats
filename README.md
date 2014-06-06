Flipperf : Android
===================

## Requirements

- AspectJ
- [BatchNetworking library](https://github.com/Flipkart/fk-android-batchnetworking)

## Installation

The library is still not available through Maven or Gradle. You need to add Flipperf project as a dependency to your project. The library works on the principles of compile time code injection. You need to install AspectJ in eclipse for using this library. AspectJ runtime needs to be bundled with the app like the android compatibility libraries. Go through the following for Eclipse and follow it step by step. 

### Eclipse as the IDE
- AspectJ for eclipse can be installed from [here](http://eclipse.org/aspectj/)
- After installing AspectJ, right click on your project -> Configure -> Convert to AspectJ Project
- Right click on your project -> Properties -> Java Build Path -> Order and Export -> Select AspectJ Runtime Library -> Press OK. This will add the AspectJ runtime to your apk.
- Sync [BatchNetworking library](https://github.com/Flipkart/fk-android-batchnetworking) as a sibling to your project. Add Volley as a dependency to BatchNetworking library. (Right click on your project -> Properties -> Android -> Library -> Add -> Browse to the Volley project directory)
- Sync the Flipperf repo as a sibling to your project repo. Add this project a library dependency to your project. (Right click on your project -> Properties -> Android -> Library -> Add -> Browse to the Flipperf project directory)
- Build Flipperf.
- Add Flipperf to the AspectJ build path of your app. (Right click on your project -> Properties -> AspectJ build -> Aspect Path -> Add Jars -> Browse to [Flipperf project directory]/bin/fk-android-flipperf.jar)


## Usage

The library works on the Contexts and Tags. A context for the library is a user flow while a tag is a technical event. For more info check this [presentation](https://docs.google.com/a/flipkart.com/presentation/d/1iWCBiX8_hDkJa7_JmGC4gCZpSJMEtFvHiR8mw_aC07w/edit#slide=id.p).

"AppInit" is set as the default context when the library is instantiated.

### Setting the context

You can set the boundries of a context by using the following function

	Flipperf.getInstance().resetGlobalContext("Name_of_the_global_context");

or 

	Flipperf.getInstance().resetGlobalContext("Name_of_the_local_context");


Resetting the global context will automatically reset the local context to null.

### Simple logging

The basic function to start tagging is:

	Flipperf.track(FlipperfTag tag, TagState tagState, String info)

#### FlipperfTag

FlipperfTag class provides some standard tags. You should ideally not create an independent tag and instead use the following function to create a child tag of one of the existing tags

	public FlipperfTag createChildTagWithName(String name)

#### State

State has three centinal values START, INTERMEDIATE AND END. START and END starts are used to calculate the time it took for the tagged event to complete. You can use custom states through the method

	public static void track(FlipperfTag tag, String tagState, JsonElement info)

#### Info

This field can be used for logging extra information. You can keep is nil if there is nothing to pass. "info" is internally a JsonElement. You can use one of the overloaded methods to pass it as a String. The String is internally converted to JsonElement.

## Logging concurrent events

Say you have events which run concurrently and have the same tags, then you should use the following method. This is internally used by the library to log the connection tag as you can have multiple request going out at the same time. 

	public void track(final FlipperfTag tag, final String state, final JsonElement info, final Object uniqueKey);

You need to provide a unique key to identify each event saparately. This is internally used to calculate the load time.

## Auto logging

Flipperf auto logs the following

- Activity.onCreate(..)
- Fragment+.onCreateView(..)
- BaseAdapter.getView(..)
- Volley connections

### Auto logging Volley connection
If you want the library to auto log the performance of a connection, you need to make the following changes in your project. The design here is specific to Volley

- FlipperfRequestQueueHolder: Your application should add a request to Volley queque through "addToVolley" method of the class which implements this interface.
- FlipperfRequst: Your Volley request class should implement this interface. It has a method "parseNetworkResponse" to be implemented, which your request class must already be implementing.





