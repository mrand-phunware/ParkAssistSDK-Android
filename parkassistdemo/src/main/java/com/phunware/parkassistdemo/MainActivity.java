package com.phunware.parkassistdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.phunware.parkassist.*;
import com.phunware.parkassist.models.ParkingZone;
import com.phunware.parkassist.models.PlateSearchResult;
import com.phunware.parkassist.networking.Callback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ParkAssistSDK mParkSDK;

    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParkSDK = new ParkAssistSDK("fb6c46aaae7eec46e88721de53b06b59", "ft-lauderdale");
        Callback<List<PlateSearchResult>> callback = new Callback<List<PlateSearchResult>>() {
            @Override
            public void onSuccess(List<PlateSearchResult> data) {
                Log.d(TAG, "Success!" + data);
            }

            @Override
            public void onFailed(Throwable e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        };

        Callback<List<ParkingZone>> zoneCallback = new Callback<List<ParkingZone>>() {
            @Override
            public void onSuccess(List<ParkingZone> data) {
                Log.d(TAG, "Success!" + data);
            }

            @Override
            public void onFailed(Throwable e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        };

        mParkSDK.searchPlates("SCL" , callback);
        mParkSDK.getZones(zoneCallback);
    }
}
