package com.flipkart.flipperf;

import com.flipkart.flipperf.model.RequestResponseModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RequestResponseModelTest {

    /**
     * Test to verify the setters and getters for {@link RequestResponseModel}
     */
    @Test
    public void testDataIntegrity() {
        RequestResponseModel requestResponseModel = new RequestResponseModel();

        requestResponseModel.setRequestId("1");
        requestResponseModel.setRequestUrl("Some_url");
        requestResponseModel.setRequestSize("20");
        requestResponseModel.setRequestMethodType("POST");
        requestResponseModel.setResponseSize("40");
        requestResponseModel.setApiSpeed(1.1);
        requestResponseModel.setResponseInputStreamError("Error reading input stream");
        requestResponseModel.setHttpExchangeErrorMessage("Error reading response");
        requestResponseModel.setResponseTime(2);
        requestResponseModel.setResponseStatusCode(200);

        Assert.assertTrue(requestResponseModel.getRequestId().equals("1"));
        Assert.assertTrue(requestResponseModel.getRequestUrl().equals("Some_url"));
        Assert.assertTrue(requestResponseModel.getRequestSize().equals("20"));
        Assert.assertTrue(requestResponseModel.getRequestMethodType().equals("POST"));
        Assert.assertTrue(requestResponseModel.getResponseSize().equals("40"));
        Assert.assertTrue(requestResponseModel.getApiSpeed() == 1.1);
        Assert.assertTrue(requestResponseModel.getResponseInputStreamError().equals("Error reading input stream"));
        Assert.assertTrue(requestResponseModel.getHttpExchangeErrorMessage().equals("Error reading response"));
        Assert.assertTrue(requestResponseModel.getResponseTime() == 2);
        Assert.assertTrue(requestResponseModel.getResponseStatusCode() == 200);
    }
}
