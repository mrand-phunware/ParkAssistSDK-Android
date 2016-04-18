package com.phunware.parkassistdemo.networking;

import com.phunware.parkassist.networking.ParkAssistNetworkingInterface;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by mrand on 4/18/16.
 */
public class FixtureNetworkingImplementation implements ParkAssistNetworkingInterface {
    private String zonesFixtureJSON = "[\n" +
            "{\n" +
            "\"counts\": {\n" +
            "        \"available\": 6,\n" +
            "        \"occupied\": 163,\n" +
            "        \"out_of_service\": 13,\n" +
            "        \"reserved\": 9,\n" +
            "        \"timestamp\": \"2013-08-19T11:45:06.8120000-04:00\",\n" +
            "        \"total\": 172,\n" +
            "        \"vacant\": 9\n" +
            "    },\n" +
            "\"id\": 1,\n" +
            "    \"name\": \"Level 2\"\n" +
            "},\n" +
            "{\n" +
            "\"counts\": {\n" +
            "        \"available\": 6,\n" +
            "        \"occupied\": 152,\n" +
            "        \"out_of_service\": 0,\n" +
            "        \"reserved\": 5,\n" +
            "        \"timestamp\": \"2013-08-19T11:44:23.2640000-04:00\",\n" +
            "        \"total\": 158,\n" +
            "        \"vacant\": 6\n" +
            "    },\n" +
            "\"id\": 2,\n" +
            "    \"name\": \"Level 3\"\n" +
            "},\n" +
            "{\n" +
            "\"counts\": {\n" +
            "        \"available\": 63,\n" +
            "        \"occupied\": 240,\n" +
            "        \"out_of_service\": 0,\n" +
            "        \"reserved\": 0,\n" +
            "        \"timestamp\": \"2013-08-19T11:46:59.4640000-04:00\",\n" +
            "        \"total\": 303,\n" +
            "        \"vacant\": 63\n" +
            "    },\n" +
            "\"id\": 3,\n" +
            "    \"name\": \"Level 4\"\n" +
            "}]";
    private String signsFixtureJSON = "[\n" +
            "{\n" +
            "\"counts\": {\n" +
            "        \"available\": 6,\n" +
            "        \"occupied\": 163,\n" +
            "        \"out_of_service\": 13,\n" +
            "        \"reserved\": 9,\n" +
            "        \"timestamp\": \"2013-08-19T11:45:06.8120000-04:00\",\n" +
            "        \"total\": 172,\n" +
            "        \"vacant\": 9\n" +
            "    },\n" +
            "\"id\": 1,\n" +
            "    \"name\": \"Level 2 Sign\"\n" +
            "},\n" +
            "{\n" +
            "\"counts\": {\n" +
            "        \"available\": 6,\n" +
            "        \"occupied\": 152,\n" +
            "        \"out_of_service\": 0,\n" +
            "        \"reserved\": 5,\n" +
            "        \"timestamp\": \"2013-08-19T11:44:23.2640000-04:00\",\n" +
            "        \"total\": 158,\n" +
            "        \"vacant\": 6\n" +
            "    },\n" +
            "\"id\": 2,\n" +
            "    \"name\": \"Level 3 Sign\"\n" +
            "}]";
    private String searchFixtureJSON = "[\n" +
            "{\n" +
            "    \"bay_group\": \"L1 - Row B\",\n" +
            "    \"bay_id\": 11021,\n" +
            "    \"map\": \"Level-1\",\n" +
            "    \"position\": {\n" +
            "\"x\": 659,\n" +
            "\"y\": 752 },\n" +
            "    \"uuid\": \"45b0d494-4b8e-45d3-ae3f-334fa1afcf5f\",\n" +
            "    \"zone\": \"Level 1\"\n" +
            "},\n" +
            "{\n" +
            "    \"bay_group\": \"L3 - Row F\",\n" +
            "    \"bay_id\": 13017,\n" +
            "    \"map\": \"Level-3\",\n" +
            "    \"position\": {\n" +
            "\"x\": 767,\n" +
            "\"y\": 660 },\n" +
            "    \"uuid\": \"755602d9-914a-432a-890a-f58c4be62bb5\",\n" +
            "    \"zone\": \"Level 3\"\n" +
            "}]";


    @Override
    public void getJSON(String path, ParkAssistJSONResponseInterface responseHandler) {
        JSONArray array;
        try {
            if (path.contains("zones")) {
                array = new JSONArray(zonesFixtureJSON);
            } else if (path.contains("signs")) {
                array = new JSONArray(signsFixtureJSON);
            } else {
                array = new JSONArray(searchFixtureJSON);
            }
            responseHandler.onSuccess(array);
        } catch (JSONException e) {
            responseHandler.onFailure(e);
        }
    }

    @Override
    public void getImage(String path, ParkAssistImageResponseInterface responseHandler) {
        responseHandler.onSuccess(new byte[8]);
    }
}
