Android Catchoom SDK
====================

Description
-----------
Android Catchoom SDK is an Android library that acts as interface between an Android Catchoom client and the Catchoom Recognition Service API. You can find an implementation example on the [Catchoom Recognition Client](https://github.com/Catchoom/android-crc "Catchoom Recognition Client") project.

Requirements
------------
To build the project or use the library, you will need Eclipse, the Android SDK and Android ADT tools.

Installation
------------
There are two main ways to use the SDK within your Android application:
* Adding the library `Catchoom-SDK.jar` to your Android project's `libs/` folder.
* Link the SDK source code in your Android application project.
    1. Open your Android app's project into Eclipse.
    2. Open your project's properties (right click on the project > properties).
    3. Select `Java Build Path` on the left column, and then click on the `Source` tab on the right.
    4. Click on `Link Source...` and use file explorer to select the Catchoom SDK project's `src` folder.
    5. Use `src-catchoom` as name and finish the linkage. From now on you should be able to use the Catchoom SDK on your project.

Usage
-----
To start using the Catchoom SDK, just instantiate the provided Catchoom object.
There are two main requests you can perform with your Catchoom object:
* `connect(String token)`: You can call connect to check a token passed by parameter against the Catchoom Recognition Service.
* `search(String token, File image)`: You can perform image recognition using search, and indicating to it the collection token against wihch you want to realize the recognition and the picture itself.

Those requests are executed asynchronously, so the operations may take several seconds (between 1 and 5 on average) depending on various factors like the Internet connection, the performance of the device, etc. In order to receive the requests' results, you must implement the `CatchoomResponseHandler` interface in your listener object.
This interface lets you override two callbacks:
* `requestCompletedResponse(int requestCode, Object responseData)`: This callback is triggered when the request has successfully been executed. It will return a `requestCode` to indicate the kind of request it has been executed (you must compare with `Catchoom.Request` codes) and the response data.
    * If the request has been a connection, the respose data will be the server's timestamp.
    * If the request has been a search, the response data will be an `ArrayList` of `CatchoomSearchResponseItem`.
* `requestFailedResponse(CatchoomErrorResponseItem responseError)`: This callback is triggered when the request has failed. It will be null if the server has not been reachable (due to connection problems, server problems, etc.) or a `CatchoomErrorResponseItem` that will facilitate to you the server response and an error description.