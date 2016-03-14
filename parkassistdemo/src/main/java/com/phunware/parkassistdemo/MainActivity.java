package com.phunware.parkassistdemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

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
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);

        final Callback thumbCallback = new Callback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                Log.d(TAG, "Success!");
                imageView.setImageBitmap(data);
            }

            @Override
            public void onFailed(Throwable e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        };

        mParkSDK = new ParkAssistSDK("fb6c46aaae7eec46e88721de53b06b59", "ft-lauderdale");
        Callback<List<PlateSearchResult>> plateCallback = new Callback<List<PlateSearchResult>>() {
            @Override
            public void onSuccess(List<PlateSearchResult> data) {
                Log.d(TAG, "Success!" + data);
                if (data.size() > 1) {
                    PlateSearchResult result = data.get(0);
                    mParkSDK.getMapImage(result.mapName, thumbCallback);
                }
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

        mParkSDK.searchPlates("SCL" , plateCallback);
        mParkSDK.getZones(zoneCallback);
    }
}
