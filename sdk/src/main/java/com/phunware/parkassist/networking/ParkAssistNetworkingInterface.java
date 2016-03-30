package com.phunware.parkassist.networking;

import android.graphics.Bitmap;

import org.json.JSONArray;

/**
 * Created by mrand on 3/29/16.
 */
public interface ParkAssistNetworkingInterface {

    void getJSON(String URL, ParkAssistJSONResponseInterface responseHandler);

    void getImage(String URL, ParkAssistImageResponseInterface responseHandler);


    interface ParkAssistJSONResponseInterface {

        void onSuccess(JSONArray response);

        void onFailure(Throwable e);
    }

    interface ParkAssistImageResponseInterface {
        void onSuccess(Bitmap image);

        void onFailure(Throwable e);
    }
}