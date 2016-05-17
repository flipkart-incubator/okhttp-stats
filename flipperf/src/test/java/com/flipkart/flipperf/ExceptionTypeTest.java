package com.flipkart.flipperf;

import com.flipkart.flipperf.toolbox.ExceptionType;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.HttpRetryException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.zip.ZipException;

/**
 * Created by anirudh.r on 17/05/16 at 10:46 PM.
 * Test for {@link ExceptionType}
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ExceptionTypeTest {

    /**
     * Test for {@link ExceptionType#getExceptionType(IOException)}
     *
     * @throws Exception
     */
    @Test
    public void testCheckExceptionType() throws Exception {

        IOException socketException = new SocketException();
        IOException protocolException = new ProtocolException();
        IOException unknownHostException = new UnknownHostException();
        IOException unknownServiceException = new UnknownServiceException();
        IOException socketTimeoutException = new SocketTimeoutException();
        IOException malformedURLException = new MalformedURLException();
        IOException httpRetryException = new HttpRetryException("Cannot retry", 400);

        //assert the exception type
        Assert.assertTrue(ExceptionType.getExceptionType(socketException) == ExceptionType.SOCKET_EXCEPTION);
        Assert.assertTrue(ExceptionType.getExceptionType(protocolException) == ExceptionType.PROTOCOL_EXCEPTION);
        Assert.assertTrue(ExceptionType.getExceptionType(unknownHostException) == ExceptionType.UNKNOWN_HOST_EXCEPTION);
        Assert.assertTrue(ExceptionType.getExceptionType(unknownServiceException) == ExceptionType.UNKNOWN_SERVICE_EXCEPTION);
        Assert.assertTrue(ExceptionType.getExceptionType(socketTimeoutException) == ExceptionType.SOCKET_TIMEOUT_EXCEPTION);
        Assert.assertTrue(ExceptionType.getExceptionType(malformedURLException) == ExceptionType.MALFORMED_URL_EXCEPTION);
        Assert.assertTrue(ExceptionType.getExceptionType(httpRetryException) == ExceptionType.HTTP_RETRY_EXCEPTION);
        Assert.assertTrue(ExceptionType.getExceptionType(new ZipException()) == ExceptionType.UNKNOWN_EXCEPTION);
    }
}
