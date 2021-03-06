package com.phunware.parkassist.networking;

import org.json.JSONArray;

/**
 * Whatever networking library you choose to use should be able to conform to these method calls
 */
public interface ParkAssistNetworkingInterface {

    /**
     * retrieve JSON response array from ParkAssist server
     * @param path relative URL path String
     * @param responseHandler capable of passing along success data or failure throwable
     */
    void getJSON(String path, ParkAssistJSONResponseInterface responseHandler);

    /**
     * retrieve image response from ParkAssist server
     * @param path relative URL path String
     * @param responseHandler capable of passing along success data or failure throwable
     */
    void getImage(String path, ParkAssistImageResponseInterface responseHandler);


    /**
     * interface for passing server data back to the application & processing into convenience objects
     */
    interface ParkAssistJSONResponseInterface {

        void onSuccess(JSONArray response);

        void onFailure(Throwable e);
    }

    /**
     * interface for passing image data received from the server back to the application layer
     */
    interface ParkAssistImageResponseInterface {
        void onSuccess(byte[] imageData);

        void onFailure(Throwable e);
    }
}