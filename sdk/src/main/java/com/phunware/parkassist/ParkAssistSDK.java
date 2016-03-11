package com.phunware.parkassist;

import android.location.Location;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phunware.parkassist.models.PlateSearchResult;
import com.phunware.parkassist.networking.Callback;
import com.phunware.parkassist.networking.ParkAssistHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private static final String SEARCH_ENDPOINT = "/search.json";
    private static final String THUMBNAIL_ENDPOINT_FORMAT = "/thumbnails/%s.jpg";
    private static final String MAP_IMG_ENDPOINT_FORMAT = "/maps/%s.png";

    private static final String PARAMS_PLATE = "plate";
    private static final String PARAMS_LATITUDE = "lat";
    private static final String PARAMS_LONGITUDE = "lon";
    private static final DecimalFormat LATLNG_FMT = new DecimalFormat("#0.000");

    public ParkAssistSDK(String appSecret, String siteSlug) {
        this.mAppSecret = appSecret;
        this.mSiteSlug = siteSlug;
    }

    public void searchPlates(String partialPlate, Callback<List<PlateSearchResult>> callback) {
        Location l = new Location("Fake provider");
        searchPlates(partialPlate, l, callback);
    }

    public void searchPlates(String partialPlate, Location location,
                             final Callback<List<PlateSearchResult>> callback) {
        if (partialPlate == null || partialPlate.length() < 3) {
            Error e = new Error("Plate search String must be at least 3 characters");
            callback.onFailed(e);
        } else {
            String latitude = LATLNG_FMT.format(location.getLatitude());
            String longitude = LATLNG_FMT.format(location.getLongitude());
            String params = PARAMS_LATITUDE + "=" + latitude + "&" + PARAMS_LONGITUDE
                    + "=" + longitude + "&" + PARAMS_PLATE +  "=" + partialPlate;
            Map<String, String> reqMap = new HashMap<>();
//            reqMap.put(PARAMS_LATITUDE, latitude);
//            reqMap.put(PARAMS_LONGITUDE, longitude);
//            reqMap.put(PARAMS_PLATE, partialPlate);
//            reqMap.put("device", getDeviceId());

            RequestParams requestParams = new RequestParams(reqMap); //params as map
            ParkAssistHttpClient.get(generateGetUrl(SEARCH_ENDPOINT, params), requestParams, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(TAG, "Status code: " + statusCode);
                    List<PlateSearchResult> results = new LinkedList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            results.add(i, new PlateSearchResult(response.getJSONObject(i)));
                        } catch (JSONException e) {
                            callback.onFailed(e);
                            return;
                        }
                    }
                    callback.onSuccess(results);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                    callback.onFailed(throwable);
                }
            });
        }

    }


    private String generateGetUrl(String urlBase, String params) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        return urlBase + "?" + "site=" + mSiteSlug + "&device=" + getDeviceId() + "&plate=SCL&signature="
                + generateSignature(params.replace('&', ','), timestamp) + "&ts=" + timestamp + "&lat=0.000&lon=0.000";
    }

    private String getDeviceId() {
        if (mDeviceId == null) {
            mDeviceId = UUID.randomUUID().toString();
        }
        return mDeviceId;
    }

    private String generateSignature(String params, long timestamp) {
        String input = mAppSecret + "device=" + getDeviceId()+ "," + params + ",site=" + mSiteSlug
                + ",ts=" + timestamp;
        String output = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            output = byteArrayToHexString(digest.digest());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return output;
    }

    private static String byteArrayToHexString(byte[] var0) {
        StringBuffer var1 = new StringBuffer();
        byte[] var2 = var0;
        int var3 = var0.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            int var6 = var5 & 255;
            if (var6 < 16) {
                var1.append("0");
            }
            var1.append(Integer.toHexString(var6));
        }

        return var1.toString();
    }
}
