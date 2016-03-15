package com.phunware.parkassistdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.phunware.parkassist.*;
import com.phunware.parkassist.models.ParkingZone;
import com.phunware.parkassist.models.PlateSearchResult;
import com.phunware.parkassist.networking.Callback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ParkAssistSDK mParkSDK;
    private List<PlateSearchResult> mSearchResults;
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView resultsRecycler = (RecyclerView)findViewById(R.id.results_recycler);
        resultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        resultsRecycler.setAdapter(new ResultsAdapter());
        final EditText plateEditText = (EditText)findViewById(R.id.plate_input);
        Button searchButton = (Button)findViewById(R.id.submit_search_button);

        mParkSDK = StaticSDK.getInstance(getString(R.string.app_secret), getString(R.string.site_slug));
        final Callback<List<PlateSearchResult>> plateCallback = new Callback<List<PlateSearchResult>>() {
            @Override
            public void onSuccess(List<PlateSearchResult> data) {
                Log.d(TAG, "Success!" + data);
                mSearchResults = data;
                resultsRecycler.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailed(Throwable e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        };


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plateEditText.clearFocus();
                mParkSDK.searchPlates(plateEditText.getText().toString(), plateCallback);
            }
        });

        final Callback thumbCallback = new Callback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                Log.d(TAG, "Success!");
            }

            @Override
            public void onFailed(Throwable e) {
                Log.e(TAG, e.getLocalizedMessage());
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

        mParkSDK.getZones(zoneCallback);
        mParkSDK.getSigns(zoneCallback);
    }

    private class ResultsHolder extends RecyclerView.ViewHolder {
        public TextView bayText;
        public TextView zoneText;

        public ResultsHolder(View v) {
            super(v);
            bayText = (TextView)v.findViewById(R.id.bay_text);
            zoneText = (TextView)v.findViewById(R.id.zone_text);
        }
    }

    private class ResultsAdapter extends RecyclerView.Adapter<ResultsHolder> {

        @Override
        public ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ResultsHolder(getLayoutInflater().inflate(R.layout.item_results, parent, false));
        }

        @Override
        public void onBindViewHolder(ResultsHolder holder, int position) {
            if (mSearchResults.size() >= position) {
                PlateSearchResult result = mSearchResults.get(position);
                holder.zoneText.setText(result.getZone());
                holder.bayText.setText(result.getBayGroup());
                holder.itemView.setTag(result);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlateSearchResult selectedPlate = (PlateSearchResult)v.getTag();
                        Intent i = new Intent(getBaseContext(), DetailActivity.class);
                        i.putExtra("map", selectedPlate.getMapName());
                        i.putExtra("UUID", selectedPlate.getUuid());
                        i.putExtra("xCoord", selectedPlate.getX());
                        i.putExtra("yCoord", selectedPlate.getY());
                        startActivity(i);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mSearchResults != null ? mSearchResults.size() : 0;
        }
    }
}
