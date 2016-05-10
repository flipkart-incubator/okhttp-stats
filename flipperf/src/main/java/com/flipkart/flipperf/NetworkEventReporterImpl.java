package com.flipkart.flipperf;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.flipkart.flipperf.model.RequestResponseModel;
import com.flipkart.flipperf.response.CountingInputStream;
import com.flipkart.flipperf.response.ResponseHandler;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
 *
 * Whenever we get a {@link NetworkEventReporterImpl#requestToBeSent(InspectorRequest)} callback, we create a {@link CurrentExecutingRequest}
 * object and add it to the {@link HashMap}.
 *
 * {@link CurrentExecutingRequest} indicates those request that are currently in execution.
 *
 * There are two cases by which we get a response callback:
 *
 * 1st Case : When response do not have Content-Length
 * Whenever we receive {@link NetworkEventReporterImpl#responseReceived(InspectorResponse)} callback, we get the corresponding {@link CurrentExecutingRequest}
 * from the {@link HashMap} using the requestId, and create a {@link RequestResponseModel} and send it accros
 *
 * 2nd Case : When response have Content-Length
 * Whenever we receive {@link NetworkEventReporterImpl#responseDataReceived(InspectorResponse, int)} callback, the same steps are repeated as in
 * {@link NetworkEventReporterImpl#responseReceived(InspectorResponse)}
 *
 * After the Response has been received, the particular {@link CurrentExecutingRequest} is removed from the {@link HashMap}.
 *
 * In case of any {@link IOException} during the {@link com.squareup.okhttp.Interceptor.Chain#proceed(Request)},
 * {@link NetworkEventReporter#httpExchangeError(InspectorRequest, IOException)} gets called with appropriate error message.
 *
 * In case of any {@link IOException} during the {@link ResponseBody#byteStream()}, {@link NetworkEventReporter#responseInputStreamError(InspectorResponse, IOException)}
 * gets called with appropriate error message.
 *
 *                    when no content length
 *                   -------------------------->  {@link NetworkEventReporterImpl#responseReceived(InspectorResponse)}
 *                  |
 *                  |
 *                  |
 *   REQUEST -------
 *                  |
 *                  |
 *                  |   with content length
 *                   -------------------------->  {@link NetworkEventReporterImpl#responseDataReceived(InspectorResponse, int)}
 *
 */
public class NetworkEventReporterImpl implements NetworkEventReporter {

    private Handler mHandler;
    private boolean mIsReporterEnabled = false;
    private NetworkCallManager mNetworkCallManager;
    private Map<String, CurrentExecutingRequest> mCurrentRequestMap;

    public Map<String, CurrentExecutingRequest> getCurrentRequestMap() {
        return mCurrentRequestMap;
    }

    @Override
    public void onInitialized(Context context, Handler handler) {
        this.mHandler = handler;
        this.mCurrentRequestMap = new HashMap<>();
        this.mNetworkCallManager = new NetworkCallManagerImpl(context, mHandler);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.mIsReporterEnabled = isEnabled;
    }

    @Override
    public boolean isReporterEnabled() {
        return mIsReporterEnabled;
    }

    @Override
    public void requestToBeSent(InspectorRequest inspectorRequest) {
        final CurrentExecutingRequest currentRequest = new CurrentExecutingRequest(inspectorRequest);
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCurrentRequestMap.put(currentRequest.getCurrentRequestId(), currentRequest);
            }
        });
    }

    @Override
    public void responseReceived(final InspectorResponse inspectorResponse) {
        final String requestId = inspectorResponse.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentRequestMap.containsKey(requestId)) {
                    CurrentExecutingRequest currentRequest = mCurrentRequestMap.get(requestId);
                    if (currentRequest.getCurrentRequestId().equals(requestId) && inspectorResponse.responseSize() != null) {
                        RequestResponseModel requestResponseModel = new RequestResponseModel();
                        requestResponseModel.setRequestId(currentRequest.getCurrentRequestId());
                        requestResponseModel.setRequestSize(currentRequest.getCurrentRequestSize());
                        requestResponseModel.setRequestUrl(currentRequest.getCurrentRequestUrl());
                        requestResponseModel.setRequestMethodType(currentRequest.getCurrentRequestMethod());
                        requestResponseModel.setResponseSize(inspectorResponse.responseSize());
                        requestResponseModel.setResponseStatusCode(inspectorResponse.statusCode());
                        requestResponseModel.setResponseTime(inspectorResponse.responseTime());
                        requestResponseModel.setApiSpeed(Double.parseDouble(requestResponseModel.getResponseSize()) / requestResponseModel.getResponseTime());

                        mNetworkCallManager.onResponseReceived(requestResponseModel);
                        mCurrentRequestMap.remove(requestId);
                    }
                }
            }
        });
    }

    @Override
    public void httpExchangeError(final InspectorRequest inspectorRequest, final IOException e) {
        final String requestId = inspectorRequest.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentRequestMap.containsKey(requestId)) {
                    CurrentExecutingRequest currentRequest = mCurrentRequestMap.get(requestId);
                    if (currentRequest.getCurrentRequestId().equals(requestId)) {
                        RequestResponseModel requestResponseModel = new RequestResponseModel();
                        requestResponseModel.setRequestId(currentRequest.getCurrentRequestId());
                        requestResponseModel.setRequestUrl(currentRequest.getCurrentRequestUrl());
                        requestResponseModel.setRequestMethodType(currentRequest.getCurrentRequestMethod());
                        requestResponseModel.setHttpExchangeErrorMessage(e.getMessage());

                        mNetworkCallManager.onHttpExchangeError(requestResponseModel);
                        mCurrentRequestMap.remove(requestId);
                    }
                }
            }
        });
    }

    @Override
    public InputStream interpretResponseStream(@Nullable InputStream inputStream, ResponseHandler responseHandler) throws IOException {
        return new CountingInputStream(inputStream, responseHandler);
    }

    @Override
    public void responseDataReceived(final InspectorResponse inspectorResponse, final int dataLength) {
        final String requestId = inspectorResponse.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentRequestMap.containsKey(requestId)) {
                    CurrentExecutingRequest currentRequest = mCurrentRequestMap.get(requestId);
                    if (currentRequest.getCurrentRequestId().equals(requestId)) {
                        RequestResponseModel requestResponseModel = new RequestResponseModel();
                        requestResponseModel.setRequestId(currentRequest.getCurrentRequestId());
                        requestResponseModel.setRequestSize(currentRequest.getCurrentRequestSize());
                        requestResponseModel.setRequestUrl(currentRequest.getCurrentRequestUrl());
                        requestResponseModel.setRequestMethodType(currentRequest.getCurrentRequestMethod());
                        requestResponseModel.setResponseSize(String.valueOf(dataLength));
                        requestResponseModel.setResponseStatusCode(inspectorResponse.statusCode());
                        requestResponseModel.setResponseTime(inspectorResponse.responseTime());
                        requestResponseModel.setApiSpeed(Double.parseDouble(requestResponseModel.getResponseSize()) / requestResponseModel.getResponseTime());

                        mNetworkCallManager.onResponseReceived(requestResponseModel);
                        mCurrentRequestMap.remove(requestId);
                    }
                }
            }
        });
    }

    @Override
    public void responseInputStreamError(InspectorResponse inspectorResponse, final IOException e) {
        final String requestId = inspectorResponse.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentRequestMap.containsKey(requestId)) {
                    CurrentExecutingRequest currentRequest = mCurrentRequestMap.get(requestId);
                    if (currentRequest.getCurrentRequestId().equals(requestId)) {
                        RequestResponseModel requestResponseModel = new RequestResponseModel();
                        requestResponseModel.setRequestId(requestId);
                        requestResponseModel.setResponseInputStreamError(e.getMessage());

                        mNetworkCallManager.onResponseInputStreamError(requestResponseModel);
                        mCurrentRequestMap.remove(requestId);
                    }
                }
            }
        });
    }

    public class CurrentExecutingRequest {
        private String mCurrentRequestId;
        private String mCurrentRequestUrl;
        private String mCurrentRequestMethod;
        private String mCurrentRequestSize;

        public CurrentExecutingRequest(InspectorRequest inspectorRequest) {
            this.mCurrentRequestId = inspectorRequest.requestId();
            this.mCurrentRequestUrl = inspectorRequest.url();
            this.mCurrentRequestMethod = inspectorRequest.method();
            this.mCurrentRequestSize = inspectorRequest.requestSize();
        }

        public String getCurrentRequestId() {
            return mCurrentRequestId;
        }

        public String getCurrentRequestMethod() {
            return mCurrentRequestMethod;
        }

        public String getCurrentRequestUrl() {
            return mCurrentRequestUrl;
        }

        public String getCurrentRequestSize() {
            return mCurrentRequestSize;
        }
    }
}
