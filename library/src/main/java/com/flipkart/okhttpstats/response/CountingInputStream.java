/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.okhttpstats.response;

import androidx.annotation.NonNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} that counts the number of bytes read.
 *
 * @author Chris Nokleberg
 * @since 2009.09.15 <b>tentative</b>
 */
public class CountingInputStream extends FilterInputStream {

    private final ResponseHandler mResponseHandler;
    private int mCount;

    public CountingInputStream(InputStream in, ResponseHandler responseHandler) {
        super(in);
        this.mResponseHandler = responseHandler;
    }

    private synchronized int checkEOF(int n) {
        if (n == -1) {
            mResponseHandler.onEOF();
        }
        return n;
    }

    @Override
    public int read() {
        try {
            int result = checkEOF(in.read());
            if (result != -1) {
                mCount++;
            }
            mResponseHandler.onRead(mCount);
            return result;
        } catch (IOException ex) {
            return 0;
        }
    }

    @Override
    public int read(@NonNull byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) {
        try {
            int result = checkEOF(in.read(b, off, len));
            if (result != -1) {
                mCount += result;
            }
            mResponseHandler.onRead(result);
            return result;
        } catch (IOException ex) {
            return 0;
        }
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Mark not supported");
    }
}
