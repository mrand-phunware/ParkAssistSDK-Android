package com.phunware.parkassistdemo;

import com.phunware.parkassist.ParkAssistSDK;
import com.phunware.parkassistdemo.networking.CustomNetworkingImplementation;

/**
 * Created by mrand on 3/15/16.
 */
public class StaticSDK {

    private static ParkAssistSDK mInstance = null;

    public static ParkAssistSDK getInstance(String secret, String slug) {
        if (mInstance == null) {
            mInstance = new ParkAssistSDK(secret, slug, new CustomNetworkingImplementation());
        }
        return mInstance;
    }
}
