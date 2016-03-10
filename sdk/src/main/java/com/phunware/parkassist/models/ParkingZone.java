package com.phunware.parkassist.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mrand on 3/10/16.
 */
public class ParkingZone {
    private SpaceCounts mSpaceCounts;
    private int mId;
    private String mZoneName;

    private static final String TAG = "ParkingZone";
    private static final String COUNT_KEY = "counts";
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";

    public static boolean isValid(JSONObject zoneJson) {
        return (zoneJson.has(ID_KEY)
            && zoneJson.has(COUNT_KEY)
            && zoneJson.has(NAME_KEY));
    }

    public ParkingZone(JSONObject zoneJson) {
        try {
            this.mId = zoneJson.getInt(ID_KEY);
            this.mZoneName = zoneJson.getString(NAME_KEY);
            this.mSpaceCounts = new SpaceCounts(zoneJson.getJSONObject(COUNT_KEY));
        } catch (JSONException e) {
            Log.e(TAG, "Error encountered parsing JSON: " + e.getLocalizedMessage());
        }
    }

    public String getZoneName() {
        return mZoneName;
    }

    public int getAvailableSpaces() {
        return mSpaceCounts.available;
    }

    private class SpaceCounts {
        /*
        Total: All bays in the zone, regardless of status
        Out of Service: Bays not currently monitored due to a malfunction or sensor downtime
        Occupied: Bays with a vehicle parked
        Vacant: Bays with no vehicle parked
        Reserved: Bays with an assigned reservation; can overlap with out of service, vacant, and occupied bays
        Available: Bays with no vehicle parked and no reservation
         */
        public int available;
        public int occupied;
        public int outOfService;
        public int reserved;
        public int total;
        public int vacant;

        private static final String AVAILABLE_KEY = "available";
        private static final String OCCUPIED_KEY = "occupied";
        private static final String OUT_OF_SVC_KEY = "out_of_service";
        private static final String RESERVED_KEY = "reserved";
        private static final String TOTAL_KEY = "total";
        private static final String VACANT_KEY = "vacant";

        public SpaceCounts(JSONObject spaceObject) throws JSONException{
            this.available = spaceObject.getInt(AVAILABLE_KEY);
            this.occupied = spaceObject.getInt(OCCUPIED_KEY);
            this.outOfService = spaceObject.getInt(OUT_OF_SVC_KEY);
            this.reserved = spaceObject.getInt(RESERVED_KEY);
            this.total = spaceObject.getInt(TOTAL_KEY);
            this.vacant = spaceObject.getInt(VACANT_KEY);
        }
    }
}
