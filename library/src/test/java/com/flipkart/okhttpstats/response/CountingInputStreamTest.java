package com.flipkart.okhttpstats.response;

import com.flipkart.okhttpstats.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 05/05/16 at 7:32 PM.
 * Test for {@link CountingInputStream}
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CountingInputStreamTest {

    /**
     * Test the working of onRead of {@link CountingInputStream#read()} and verify {@link ResponseHandler#onRead(int)} gets called once
     *
     * @throws IOException
     */
    @Test
    public void testOnRead() throws IOException {

        ResponseHandler responseHandler = mock(ResponseHandler.class);
        String myString = "Hello! How are you";

        InputStream is = new ByteArrayInputStream(myString.getBytes());
        CountingInputStream countingInputStream = new CountingInputStream(is, responseHandler);
        countingInputStream.read();

        //verify onread gets called once
        verify(responseHandler, timeout(1)).onRead(anyInt());
    }

    /**
     * Test the working of onRead of {@link CountingInputStream#read(byte[])} and verify {@link ResponseHandler#onRead(int)} gets called once
     *
     * @throws IOException
     */
    @Test
    public void testOnReadWithByte() throws IOException {
        ResponseHandler responseHandler = mock(ResponseHandler.class);
        String myString = "Hello! How are you";

        InputStream is = new ByteArrayInputStream(myString.getBytes());
        CountingInputStream countingInputStream = new CountingInputStream(is, responseHandler);
        countingInputStream.read(myString.getBytes());

        //verify onread gets called once
        verify(responseHandler, timeout(1)).onRead(anyInt());
    }

    /**
     * Test the working of onRead of {@link CountingInputStream#markSupported()}
     *
     * @throws IOException
     */
    @Test
    public void testOnMark() throws IOException {
        ResponseHandler responseHandler = mock(ResponseHandler.class);
        String myString = "Hello! How are you";
        InputStream is = new ByteArrayInputStream(myString.getBytes());

        CountingInputStream countingInputStream = new CountingInputStream(is, responseHandler);
        boolean markSupported = countingInputStream.markSupported();

        Assert.assertTrue(!markSupported);
    }

    /**
     * Test the working of onRead of {@link CountingInputStream#reset()} and verify {@link UnsupportedOperationException} is thrown.
     *
     * @throws IOException
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testReset() throws IOException {
        ResponseHandler responseHandler = mock(ResponseHandler.class);
        String myString = "Hello! How are you";
        InputStream is = new ByteArrayInputStream(myString.getBytes());

        CountingInputStream countingInputStream = new CountingInputStream(is, responseHandler);
        countingInputStream.reset();
    }
}
