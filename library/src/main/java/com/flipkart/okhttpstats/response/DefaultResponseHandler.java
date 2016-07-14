/*
 *
 *  * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.flipkart.okhttpstats.response;

/**
 * Default implementation of {@link ResponseHandler}
 */
public class DefaultResponseHandler implements ResponseHandler {
    private final ResponseCallback mResponseCallback;
    private int mBytesRead = 0;

    public DefaultResponseHandler(ResponseCallback responseCallback) {
        mResponseCallback = responseCallback;
    }

    @Override
    public void onRead(int numBytes) {
        mBytesRead += numBytes;
    }

    @Override
    public void onEOF() {
        mResponseCallback.onEOF(mBytesRead);
    }

    public interface ResponseCallback {
        void onEOF(long bytesRead);
    }
}
