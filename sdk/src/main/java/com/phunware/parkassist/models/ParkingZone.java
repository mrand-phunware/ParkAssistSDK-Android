package com.phunware.parkassist.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parking Zone/Parking Sign details model
 *
 */
//sample result:
//{
//        "counts": {
//        "available": 6,
//        "occupied": 163,
//        "out_of_service": 13,
//        "reserved": 9,
//        "timestamp": "2013-08-19T11:45:06.8120000-04:00",
//        "total": 172,
//        "vacant": 9
//        },
//        "id": 1,
//        "name": "Level 2"
//        }
public class ParkingZone {
    private SpaceCounts mSpaceCounts;
    private int mId;
    private String mZoneName;

    private static final String TAG = "ParkingZone";
    private static final String COUNT_KEY = "counts";
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";

    /**
     * creates ParkingZone object from JSON returned from server
     * @param zoneJson JSONObject from ParkAssist server
     */
    public ParkingZone(JSONObject zoneJson) throws JSONException{
        this.mId = zoneJson.getInt(ID_KEY);
        this.mZoneName = zoneJson.getString(NAME_KEY);
        this.mSpaceCounts = new SpaceCounts(zoneJson.getJSONObject(COUNT_KEY));
    }

    /**
     *
     * @return zone name or parking sign name
     */
    public String getName() {
        return mZoneName;
    }

    /**
     *
     * @return zone or parking sign identifier
     */
    public int getId() {
        return mId;
    }

    /**
     *
     * @return number of available bays in zone (Bays with no vehicle parked and no reservatio)
     */
    public int getAvailableSpaces() {
        return mSpaceCounts.available;
    }

    /**
     *
     * @return total number of spaces in zone
     */
    public int getTotalSpaces() {
        return mSpaceCounts.total;
    }

    /**
     *
     * @return number of occupied spaces in zone
     */
    public int getOccupiedSpaces() {
        return mSpaceCounts.occupied;
    }

    /**
     *
     * @return number of spaces in zone that are out of service
     */
    public int getOutOfServiceSpaces() {
        return mSpaceCounts.outOfService;
    }

    /**
     *
     * @return number of reserved spaces in zone
     */
    public int getReservedSpaces() {
        return mSpaceCounts.reserved;
    }

    private class SpaceCounts {
        /*
        Total: All bays in the zone, regardless of status
        Out of Service: Bays not currently monitored due to a malfunction or sensor downtime
        Occupied: Bays with a vehicle parked
        Vacant: Bays with no vehicle parked
        Reserved: Bays with an assigned reservation; can overlap with out of service, vacant, and
        occupied bays
        Available: Bays with no vehicle parked and no reservation
         */
        protected int available;
        protected int occupied;
        protected int outOfService;
        protected int reserved;
        protected int total;
        protected int vacant;

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
