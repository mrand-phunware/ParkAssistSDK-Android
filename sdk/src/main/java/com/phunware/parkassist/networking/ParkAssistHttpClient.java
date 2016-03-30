package com.phunware.parkassist.networking;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;


import cz.msebera.android.httpclient.Header;

/**
 * Created by mrand on 3/10/16.
 */
public class ParkAssistHttpClient implements ParkAssistNetworkingInterface {
    private static final String BASE_URL = "https://insights.parkassist.com/find_your_car";

    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    public void getJSON(String URL, final ParkAssistJSONResponseInterface responseHandler) {
        JsonHttpResponseHandler httpHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                responseHandler.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                responseHandler.onFailure(throwable);
            }
        };
        client.get(getAbsoluteUrl(URL), httpHandler);
    }

    @Override
    public void getImage(String URL, final ParkAssistImageResponseInterface responseHandler) {
        BinaryHttpResponseHandler imageHandler = new BinaryHttpResponseHandler() {
            @Override
            public String[] getAllowedContentTypes() {
                return new String[]{"image/jpeg", "text/html; charset=utf-8", "image/png"};
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                responseHandler.onSuccess(binaryData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                responseHandler.onFailure(error);
            }
        };
        client.get(getAbsoluteUrl(URL), imageHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
