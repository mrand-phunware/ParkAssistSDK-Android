package com.phunware.parkassist;

import android.location.Location;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phunware.parkassist.models.ParkingZone;
import com.phunware.parkassist.models.PlateSearchResult;
import com.phunware.parkassist.networking.Callback;
import com.phunware.parkassist.networking.ParkAssistHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.MessageDigest;
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
    private static final String ZONES_ENDPOING = "/zones.json";

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
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(PARAMS_LATITUDE, latitude);
            paramMap.put(PARAMS_LONGITUDE, longitude);
            paramMap.put(PARAMS_PLATE, partialPlate);
            RequestParams requestParams = new RequestParams(); // intentionally empty
            ParkAssistHttpClient.get(generatePlateGetUrl(SEARCH_ENDPOINT, paramMap), requestParams,
                    new JsonHttpResponseHandler() {

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

    public void getZones(Callback<List<ParkingZone>> callback) {
        Location fakeLocation = new Location("fake provider");
        getZones(fakeLocation, callback);
    }

    public void getZones(Location location, final Callback<List<ParkingZone>> callback) {
        String latitude = LATLNG_FMT.format(location.getLatitude());
        String longitude = LATLNG_FMT.format(location.getLongitude());
        RequestParams requestParams = new RequestParams(); // intentionally empty
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(PARAMS_LATITUDE, latitude);
        paramMap.put(PARAMS_LONGITUDE, longitude);
        ParkAssistHttpClient.get(generateZoneGetURL(ZONES_ENDPOING, paramMap), requestParams,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d(TAG, "Status code: " + statusCode);
                        List<ParkingZone> results = new LinkedList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                if (ParkingZone.isValid(response.getJSONObject(i))) {
                                    results.add(new ParkingZone(response.getJSONObject(i)));
                                } else {
                                    Log.d(TAG, "Invalid parking zone json: " + response.get(i));
                                }
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

    private String generatePlateGetUrl(String urlBase, Map<String, String> params) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        return urlBase + "?" + "site=" + mSiteSlug + "&device=" + getDeviceId() + "&" + PARAMS_PLATE
        + "=" + params.get(PARAMS_PLATE) + "&signature=" + generatePlateSignature(params, timestamp)
                + "&ts=" + timestamp + "&" + PARAMS_LATITUDE + "=" + params.get(PARAMS_LATITUDE)
                + "&" + PARAMS_LONGITUDE + "=" + params.get(PARAMS_LONGITUDE);
    }

    private String generateZoneGetURL(String urlBase, Map<String, String> params) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        return urlBase + "?" + "site=" + mSiteSlug + "&device=" + getDeviceId() + "&signature="
                + generateSignature(params, timestamp) + "&ts=" + timestamp + "&" + PARAMS_LATITUDE
                + "=" + params.get(PARAMS_LATITUDE) + "&" + PARAMS_LONGITUDE + "="
                + params.get(PARAMS_LONGITUDE);
    }

    private String getDeviceId() {
        if (mDeviceId == null) {
            mDeviceId = UUID.randomUUID().toString();
        }
        return mDeviceId;
    }

    private String generatePlateSignature(Map<String, String> params, long timestamp) {
        String input = mAppSecret + "device=" + getDeviceId()+ "," + PARAMS_LATITUDE + "="
                + params.get(PARAMS_LATITUDE) + "," + PARAMS_LONGITUDE + "="
                + params.get(PARAMS_LONGITUDE) + "," + PARAMS_PLATE + "=" + params.get(PARAMS_PLATE)
                + ",site=" + mSiteSlug + ",ts=" + timestamp;
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

    private String generateSignature(Map<String, String> params, long timestamp) {
        String input = mAppSecret + "device=" + getDeviceId()+ "," + PARAMS_LATITUDE + "="
                + params.get(PARAMS_LATITUDE) + "," + PARAMS_LONGITUDE + "="
                + params.get(PARAMS_LONGITUDE) + ",site=" + mSiteSlug + ",ts=" + timestamp;
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

    private static String byteArrayToHexString(byte[] startBytes) {
        StringBuilder builder = new StringBuilder();
        int length = startBytes.length;

        for (int i = 0; i < length; ++i) {
            byte b = startBytes[i];
            int hexInt = b & 255;
            if (hexInt < 16) {
                builder.append("0");
            }
            builder.append(Integer.toHexString(hexInt));
        }

        return builder.toString();
    }
}
