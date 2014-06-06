Flipperf - Performance evaluation through logging: Android
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

	public static void track(FlipperfTag tag, TagState tagState, String info)

