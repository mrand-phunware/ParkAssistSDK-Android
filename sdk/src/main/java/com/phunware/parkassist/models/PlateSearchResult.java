package com.phunware.parkassist.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * model object for search results from ParkAssist API
 */
public class PlateSearchResult {
    private String mBayGroup;
    private int mBayId;
    private String mUuid;
    private String mZone;
    private String mMapName;
    private Position mPosition;

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

    /**
     * Create search result object from server JSON
     * @param jsonObject JSONObject returned from ParkAssist API
     */
    public PlateSearchResult(JSONObject jsonObject) throws JSONException{
        this.mBayGroup = jsonObject.getString(BAY_GROUP_KEY);
        this.mBayId = jsonObject.getInt(BAY_ID_KEY);
        this.mUuid = jsonObject.getString(UUID_KEY);
        this.mMapName = jsonObject.getString(MAP_NAME_KEY);
        this.mZone = jsonObject.getString(ZONE_KEY);
        this.mPosition = new Position(jsonObject.getJSONObject(POSITION_KEY));
    }

    /**
     *
     * @return name of bay group
     */
    public String getBayGroup() {
        return mBayGroup;
    }

    /**
     * @return bay identifier
     */
    public int getBayId() {
        return mBayId;
    }

    /**
     *
     * @return unique identifier used in image calls
     */
    public String getUuid() {
        return mUuid;
    }

    /**
     *
     * @return parking zone
     */
    public String getZone() {
        return mZone;
    }

    /**
     *
     * @return name of map
     */
    public String getMapName() {
        return mMapName;
    }

    /**
     * @return Number of horizontal pixels from top left of vehicle location on map image
     */
    public int getX() {
        return mPosition.x;
    }

    /**
     * @return  Number of vertical pixels from top left of vehicle location on map image
     */
    public int getY() {
        return mPosition.y;
    }

    protected class Position {
        protected int x;
        protected int y;

        protected Position(JSONObject positionObject) {
            try {
                this.x = positionObject.getInt("x");
                this.y = positionObject.getInt("y");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getLocalizedMessage());
            }
        }
    }
}
