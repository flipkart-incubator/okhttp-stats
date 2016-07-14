/*
 * The MIT License
 *
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.flipkart.okhttpstats.response;

import android.support.annotation.NonNull;

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
    public int read() throws IOException {
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
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
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
    public void reset() throws IOException {
        throw new UnsupportedOperationException("Mark not supported");
    }
}
