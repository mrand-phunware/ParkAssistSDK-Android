package com.phunware.parkassist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import com.phunware.parkassist.models.ParkingZone;
import com.phunware.parkassist.models.PlateSearchResult;
import com.phunware.parkassist.networking.Callback;
import com.phunware.parkassist.networking.ParkAssistNetworkingInterface;

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


/**
 * Public class for interacting with ParkAssist Web api
 * Developer should create a single instance of ParkAssistSDK with their app secret and site slug
 * and use that instance for all calls to the API
 *
 */
public class ParkAssistSDK {

    private String mAppSecret;
    private String mSiteSlug;
    private String mDeviceId;
    private ParkAssistNetworkingInterface httpClient;

    private static final String TAG = "ParkAssistSDK";
    private static final String SEARCH_ENDPOINT = "/search.json";
    private static final String THUMBNAIL_ENDPOINT_FORMAT = "/thumbnails/%s.jpg";
    private static final String MAP_IMG_ENDPOINT_FORMAT = "/maps/%s.png";
    private static final String ZONES_ENDPOINT = "/zones.json";
    private static final String SIGNS_ENDPOINT = "/signs.json";

    private static final String PARAMS_PLATE = "plate";
    private static final String PARAMS_LATITUDE = "lat";
    private static final String PARAMS_LONGITUDE = "lon";
    private static final DecimalFormat LATLNG_FMT = new DecimalFormat("#0.000");

    /**
     * Creates an instance of ParkAssistSDK for communicating with the ParkAssist servers securely
     *
     * @param appSecret Your shared secret key
     * @param siteSlug String identifying the parking facility
     * @param httpClient Custom implementation of ParkAssistNetworkingInterface
     */
    public ParkAssistSDK(String appSecret, String siteSlug, ParkAssistNetworkingInterface httpClient) {
        this.mAppSecret = appSecret;
        this.mSiteSlug = siteSlug;
        this.httpClient = httpClient;
    }

    /**
     * Given a partial license plate, returns up to 3 possible matches from parking garage along
     * with details allowing access to vehicle thumbnail images or parking maps
     *
     * @param partialPlate The license plate text to query, eg "ABC123", "XYZ". The plate text must
     *                     only be comprised of uppercase Alphanumeric characters (do not include
     *                     whitespace, punctuation)
     * @param callback Callback object to handle success or failure. Success block will have a
     *                 list of up to 3 possible @PlateSearchResult items returned from the server
     */
    public void searchPlates(String partialPlate, Callback<List<PlateSearchResult>> callback) {
        Location l = new Location("Fake provider");
        searchPlates(partialPlate, l, callback);
    }

    /**
     * Given a partial license plate, returns up to 3 possible matches from parking garage along
     * with details allowing access to vehicle thumbnail images or parking maps
     *
     * @param partialPlate The license plate text to query, eg "ABC123", "XYZ". The plate text must
     *                     only be comprised of uppercase Alphanumeric characters (do not include
     *                     whitespace, punctuation)
     * @param location Location object with the latitude and longitude of the device making the
     *                 request
     * @param callback Callback object to handle success or failure. Success block will have a
     *                 list of up to 3 possible @PlateSearchResult items returned from the server
     *
     */
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
            httpClient.getJSON(generatePlateGetUrl(SEARCH_ENDPOINT, paramMap),
                    new ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface() {

                        @Override
                        public void onSuccess(JSONArray response) {
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
                        public void onFailure(Throwable throwable) {
                            callback.onFailed(throwable);
                        }
                    });
        }

    }

    /**
     * returns the vehicle counts in each zone at the property.
     *
     * @param callback Callback object to handle success or failure. Success block includes a list
     *                 of ParkingZone results returned from the server
     */
    public void getZones(Callback<List<ParkingZone>> callback) {
        Location fakeLocation = new Location("fake provider");
        getZones(fakeLocation, callback);
    }

    /**
     * returns the vehicle counts in each zone at the property.
     *
     * @param location Location object with the latitude and longitude of the device making the
     *                 request
     * @param callback Callback object to handle success or failure. Success block includes a list
     *                 of ParkingZone results returned from the server
     */
    public void getZones(Location location, final Callback<List<ParkingZone>> callback) {
        String latitude = LATLNG_FMT.format(location.getLatitude());
        String longitude = LATLNG_FMT.format(location.getLongitude());
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(PARAMS_LATITUDE, latitude);
        paramMap.put(PARAMS_LONGITUDE, longitude);
        httpClient.getJSON(generateGetURL(ZONES_ENDPOINT, paramMap),
                new ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        List<ParkingZone> results = new LinkedList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                results.add(new ParkingZone(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                callback.onFailed(e);
                                return;
                            }
                        }
                        callback.onSuccess(results);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailed(throwable);
                    }
                });
    }

    /**
     * returns information about the signs at the parking facility in ParkingZone format
     *
     * @param callback Callback object to handle success or failure. Success block includes a list
     *                 of ParkingZone results returned from the server
     */
    public void getSigns(Callback<List<ParkingZone>> callback) {
        Location l = new Location("fake provider");
        getSigns(l, callback);
    }

    /**
     * returns information about the signs at the parking facility in ParkingZone format
     *
     *
     * @param location Location object with the latitude and longitude of the device making the
     *                 request
     * @param callback Callback object to handle success or failure. Success block includes a list
     *                 of ParkingZone results returned from the server
     */
    public void getSigns(Location location, final Callback<List<ParkingZone>> callback) {
        String latitude = LATLNG_FMT.format(location.getLatitude());
        String longitude = LATLNG_FMT.format(location.getLongitude());
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(PARAMS_LATITUDE, latitude);
        paramMap.put(PARAMS_LONGITUDE, longitude);
        httpClient.getJSON(generateGetURL(SIGNS_ENDPOINT, paramMap),
                new ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        List<ParkingZone> results = new LinkedList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                results.add(new ParkingZone(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                callback.onFailed(e);
                                return;
                            }
                        }
                        callback.onSuccess(results);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailed(throwable);
                    }
                });
    }

    /**
     * returns a low-resolution image of a search result vehicle
     *
     * @param uuid Unique identifier associated with search result
     *
     * @param callback Callback object, success block takes Bitmap image from server
     */
    public void getVehicleThumbnail(String uuid, Callback<Bitmap> callback) {
        Location l = new Location("fake provider");
        getVehicleThumbnail(l, uuid, callback);
    }

    /**
     * returns a low-resolution image of a search result vehicle
     *
     * @param location Location object representing the latitude and longitude of device making
     *                 the request
     *
     * @param uuid Unique identifier associated with search result
     *
     * @param callback Callback object, success block takes Bitmap image from server
     */
    public void getVehicleThumbnail(Location location, String uuid, final Callback<Bitmap> callback) {
        String latitude = LATLNG_FMT.format(location.getLatitude());
        String longitude = LATLNG_FMT.format(location.getLongitude());
        Map<String, String> params = new HashMap<>();
        params.put(PARAMS_LATITUDE, latitude);
        params.put(PARAMS_LONGITUDE, longitude);
        httpClient.getImage(generateGetURL(String.format(THUMBNAIL_ENDPOINT_FORMAT, uuid), params),
                new ParkAssistNetworkingInterface.ParkAssistImageResponseInterface() {
                    @Override
                    public void onSuccess(byte[] imageData) {
                        Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        callback.onSuccess(image);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        callback.onFailed(error);
                    }
                });
    }

    /**
     * returns an image of a map at the parking facility
     *
     * @param mapName Name of the requested map
     * @param callback Callback object, success block receives a Bitmap image of the map
     */
    public void getMapImage(String mapName, Callback<Bitmap> callback) {
        Location l = new Location("fake provider");
        getMapImage(l, mapName, callback);
    }

    /**
     * returns an image of a map at the parking facility
     *
     * @param location Location object representing latitude and longitude of device making the
     *                 request
     * @param mapName Name of the requested map
     * @param callback Callback object, success block receives a Bitmap image of the map
     */
    public void getMapImage(Location location, String mapName, final Callback<Bitmap> callback) {
        String latitude = LATLNG_FMT.format(location.getLatitude());
        String longitude = LATLNG_FMT.format(location.getLongitude());
        Map<String, String> params = new HashMap<>();
        params.put(PARAMS_LATITUDE, latitude);
        params.put(PARAMS_LONGITUDE, longitude);
        httpClient.getImage(generateGetURL(String.format(MAP_IMG_ENDPOINT_FORMAT, mapName), params),
                new ParkAssistNetworkingInterface.ParkAssistImageResponseInterface() {

                    @Override
                    public void onSuccess(byte[] imageData) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        callback.onSuccess(bitmap);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        callback.onFailed(error);
                    }
                });
    }

    /*
    Private helper methods
     */
    private String generatePlateGetUrl(String urlBase, Map<String, String> params) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        return urlBase + "?" + "site=" + mSiteSlug + "&device=" + getDeviceId() + "&" + PARAMS_PLATE
        + "=" + params.get(PARAMS_PLATE) + "&signature=" + generatePlateSignature(params, timestamp)
                + "&ts=" + timestamp + "&" + PARAMS_LATITUDE + "=" + params.get(PARAMS_LATITUDE)
                + "&" + PARAMS_LONGITUDE + "=" + params.get(PARAMS_LONGITUDE);
    }

    private String generateGetURL(String urlBase, Map<String, String> params) {
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
