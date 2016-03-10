package com.phunware.parkassist.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mrand on 3/10/16.
 */
public class PlateSearchResult {
    public String bayGroup;
    public int bayId;
    public String uuid;
    public String zone;
    public String mapName;
    public Position position;

    private static final String BAY_GROUP_KEY = "bay_group";
    private static final String BAY_ID_KEY = "bay_id";
    private static final String MAP_NAME_KEY = "map";
    private static final String POSITION_KEY = "position";
    private static final String UUID_KEY = "uuid";
    private static final String ZONE_KEY = "zone";
    private static final String TAG = "PlateSearchResult";

    /*
    Sample plate search JSON return:
    {
        "bay_group": "L1 - Row B",
        "bay_id": 11021,
        "map": "Level-1",
        "position": {
            "x": 659,
            "y": 752 },
        "uuid": "45b0d494-4b8e-45d3-ae3f-334fa1afcf5f",
        "zone": "Level 1"
    }
     */

    public PlateSearchResult(JSONObject jsonObject) {
        try {
            this.bayGroup = jsonObject.getString(BAY_GROUP_KEY);
            this.bayId = jsonObject.getInt(BAY_ID_KEY);
            this.uuid = jsonObject.getString(UUID_KEY);
            this.mapName = jsonObject.getString(MAP_NAME_KEY);
            this.zone = jsonObject.getString(ZONE_KEY);
            this.position = new Position(jsonObject.getJSONObject(POSITION_KEY));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getLocalizedMessage());
        }
    }

    public class Position {
        public int x;
        public int y;

        public Position(JSONObject positionObject) {
            try {
                this.x = positionObject.getInt("x");
                this.y = positionObject.getInt("y");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getLocalizedMessage());
            }
        }
    }
}
