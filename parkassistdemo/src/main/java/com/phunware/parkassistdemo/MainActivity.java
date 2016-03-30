package com.phunware.parkassistdemo;

import android.content.Intent;
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
    private List<ParkingZone> mZones;
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView resultsRecycler = (RecyclerView)findViewById(R.id.results_recycler);
        resultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        resultsRecycler.setAdapter(new ResultsAdapter());
        final RecyclerView zoneRecycler = (RecyclerView)findViewById(R.id.zone_recycler);
        zoneRecycler.setLayoutManager(new LinearLayoutManager(this));
        zoneRecycler.setAdapter(new ZoneAdapter());
        final EditText plateEditText = (EditText)findViewById(R.id.plate_input);
        Button searchButton = (Button)findViewById(R.id.submit_search_button);
        Button zoneButton = (Button)findViewById(R.id.zone_button);
        Button signButton = (Button)findViewById(R.id.sign_button);

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

        final Callback<List<ParkingZone>> zoneCallback = new Callback<List<ParkingZone>>() {
            @Override
            public void onSuccess(List<ParkingZone> data) {
                mZones = data;
                zoneRecycler.getAdapter().notifyDataSetChanged();
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
        zoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParkSDK.getZones(zoneCallback);
            }
        });
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParkSDK.getSigns(zoneCallback);
            }
        });

        mParkSDK.getZones(zoneCallback);
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

    private class ZoneHolder extends RecyclerView.ViewHolder {
        public TextView zoneName;
        public TextView available;
        public TextView reserved;
        public TextView total;

        public ZoneHolder(View v) {
            super(v);
            zoneName = (TextView)v.findViewById(R.id.zone_name);
            available = (TextView)v.findViewById(R.id.available_spots);
            reserved = (TextView)v.findViewById(R.id.reserved_spots);
            total = (TextView)v.findViewById(R.id.total_spots);
        }
    }

    private class ZoneAdapter extends RecyclerView.Adapter<ZoneHolder> {

        @Override
        public ZoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ZoneHolder(getLayoutInflater().inflate(R.layout.item_zone, parent, false));
        }

        @Override
        public void onBindViewHolder(ZoneHolder holder, int position) {
            if (mZones.size() >= position) {
                ParkingZone result = mZones.get(position);
                holder.zoneName.setText(result.getName());
                holder.available.setText("Available: " + result.getAvailableSpaces());
                holder.reserved.setText("Reserved: " + result.getReservedSpaces());
                holder.total.setText("Total: " + result.getTotalSpaces());
            }
        }

        @Override
        public int getItemCount() {
            return mZones != null ? mZones.size() : 0;
        }
    }
}
