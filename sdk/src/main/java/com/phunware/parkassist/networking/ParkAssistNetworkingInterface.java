package com.phunware.parkassist.networking;

import android.graphics.Bitmap;

import org.json.JSONArray;

/**
 * Whatever networking library you choose to use should be able to conform to these method calls
 */
public interface ParkAssistNetworkingInterface {

    /**
     * retrieve JSON response array from ParkAssist server
     * @param URL relative URL path String
     * @param responseHandler capable of passing along success data or failure throwable
     */
    void getJSON(String URL, ParkAssistJSONResponseInterface responseHandler);

    /**
     * retrieve image response from ParkAssist server
     * @param URL relative URL path String
     * @param responseHandler capable of passing along success data or failure throwable
     */
    void getImage(String URL, ParkAssistImageResponseInterface responseHandler);


    /**
     * interface for passing server data back to the application & processing into convenience objects
     */
    interface ParkAssistJSONResponseInterface {

        void onSuccess(JSONArray response);

        void onFailure(Throwable e);
    }

    /**
     * interface for passing Bitmaps received from the server back to the application layer
     */
    interface ParkAssistImageResponseInterface {
        void onSuccess(Bitmap image);

        void onFailure(Throwable e);
    }
}