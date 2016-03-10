package com.phunware.parkassist;

import android.content.Loader;
import android.location.Location;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phunware.parkassist.models.PlateSearchResult;
import com.phunware.parkassist.networking.Callback;
import com.phunware.parkassist.networking.ParkAssistHttpClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mrand on 3/10/16.
 */
public class ParkAssistSDK {

    private String mAppSecret;
    private String mSiteSlug;
    private String mDeviceId;

    private static final String TAG = "ParkAssistSDK";
    private static final String SEARCH_ENDPOINT =
            "/search.json";
    private static final String THUMBNAIL_ENDPOINT_FORMAT =
            "https://insights.parkassist.com/find_your_car/thumbnails/%s.jpg";
    private static final String MAP_IMG_ENDPOINT_FORMAT =
            "https://insights.parkassist.com/find_your_car/maps/%s.png";

    private static final String PARAMS_PLATE = "plate";
    private static final String PARAMS_LATITUDE = "lat";
    private static final String PARAMS_LONGITUDE = "lon";

    public ParkAssistSDK(String appSecret, String siteSlug) {
        this.mAppSecret = appSecret;
        this.mSiteSlug = siteSlug;
    }

    public void searchPlates(String partialPlate, Callback<List<PlateSearchResult>> callback) {
        Location l = new Location("Fake provider"); //TODO: check provider requirements
        searchPlates(partialPlate, l, callback);
    }

    public void searchPlates(String partialPlate, Location location,
                             final Callback<List<PlateSearchResult>> callback) {
        if (partialPlate == null || partialPlate.length() < 3) {
            Error e = new Error("Plate search String must be at least 3 characters");
            callback.onFailed(e);
        } else {
            String params = PARAMS_LATITUDE + "=" + location.getLatitude() + "&" + PARAMS_LONGITUDE
                    + "=" + location.getLongitude() + "&" + PARAMS_PLATE +  "=" + partialPlate;
            generateGetUrl(SEARCH_ENDPOINT, params);
            RequestParams requestParams = new RequestParams()//params as map
            ParkAssistHttpClient.get(SEARCH_ENDPOINT, requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    callback.onFailed(error);
                }
            });
        }

    }


    private String generateGetUrl(String urlBase, String params) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        return urlBase + "?" + "device=" + getDeviceId() + "&" + params + "&signature=" + generateSignature(params, timestamp)
                + "&site=" + mSiteSlug + "&ts=" + timestamp;
    }

    private String getDeviceId() {
        if (mDeviceId == null) {
            mDeviceId = UUID.randomUUID().toString();
        }
        return mDeviceId;
    }

    private String generateSignature(String params, long timestamp) {
        String input = mAppSecret + "device=" + getDeviceId()+ "," + params + ",site=" + mSiteSlug + ",ts=" + timestamp;
        String output = "";
        try {
             output = new String(MessageDigest.getInstance("MD5").digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return output;
    }
}
