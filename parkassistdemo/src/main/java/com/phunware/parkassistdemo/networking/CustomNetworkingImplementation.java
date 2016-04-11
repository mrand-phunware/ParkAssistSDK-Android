package com.phunware.parkassistdemo.networking;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.phunware.parkassist.networking.ParkAssistNetworkingInterface;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mrand on 4/8/16.
 */
public class CustomNetworkingImplementation implements ParkAssistNetworkingInterface {
    private static final String BASE_URL = "https://insights.parkassist.com/find_your_car";

    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    public void getJSON(String path, final ParkAssistJSONResponseInterface responseHandler) {
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
        client.get(getAbsoluteUrl(path), httpHandler);
    }

    @Override
    public void getImage(String path, final ParkAssistImageResponseInterface responseHandler) {
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
        client.get(getAbsoluteUrl(path), imageHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
