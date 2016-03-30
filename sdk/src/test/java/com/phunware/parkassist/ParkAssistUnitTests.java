package com.phunware.parkassist;

import com.phunware.parkassist.models.ParkingZone;
import com.phunware.parkassist.models.PlateSearchResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by mrand on 3/30/16.
 */
public class ParkAssistUnitTests {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void parseValidSearchResult() throws Exception {
        //given
        JSONObject validObject = new JSONObject();
        validObject.put("bay_group", "Level 1");
        validObject.put("bay_id", 1);
        validObject.put("map", "level_1");
        validObject.put("uuid", "1111-2222-3333");
        validObject.put("zone", "Level1");
        JSONObject positionObject = new JSONObject();
        positionObject.put("x", 300);
        positionObject.put("y", 300);
        validObject.put("position", positionObject);

        //when
        PlateSearchResult result = new PlateSearchResult(validObject);

        //then
        assertEquals(result.getBayGroup(), validObject.getString("bay_group"));
        assertEquals(result.getUuid(), validObject.getString("uuid"));
        assertEquals(result.getMapName(), validObject.get("map"));
        assertEquals(result.getX(), validObject.getJSONObject("position").getInt("x"));
        assertEquals(result.getY(), validObject.getJSONObject("position").getInt("y"));
    }

    @Test
    public void parseInvalidSearchResult() throws Exception {
        //given
        JSONObject invalidObject = new JSONObject();
        invalidObject.put("bay_group", "Level 1");
        invalidObject.put("bay_id", 1);
        invalidObject.put("map", "level_1");
        invalidObject.put("zone", "Level1");
        JSONObject positionObject = new JSONObject();
        positionObject.put("x", 300);
        positionObject.put("y", 300);
        invalidObject.put("position", positionObject);

        exception.expect(JSONException.class);
        PlateSearchResult result = new PlateSearchResult(invalidObject);

        //when
//        JSONException uuidException = null;
//        try {
//            PlateSearchResult result = new PlateSearchResult(invalidObject);
//        } catch (JSONException e) {
//            uuidException = e;
//        } finally {
//            //then
//            assertNotNull(uuidException);
//        }
    }

    @Test
    public void parseValidZone() throws Exception {
        //given
        JSONObject validZone = new JSONObject();
        JSONObject countsObject = new JSONObject();
        countsObject.put("available", 5);
        countsObject.put("occupied", 100);
        countsObject.put("out_of_service", 3);
        countsObject.put("reserved", 0);
        countsObject.put("timestamp", "2013-08-19T11:45:06.8120000-04:00");
        countsObject.put("total", 200);
        countsObject.put("vacant", 80);
        validZone.put("counts", countsObject);
        validZone.put("id", 3);
        validZone.put("name", "Level 3");

        //when
        ParkingZone zone = new ParkingZone(validZone);

        //then
        assertEquals(zone.getId(), validZone.getInt("id"));
        assertEquals(zone.getAvailableSpaces(), validZone.getJSONObject("counts").getInt("available"));
    }

    @Test
    public void parseInvalidZone() throws Exception {
        //given
        JSONObject invalidZone = new JSONObject();
        JSONObject countsObject = new JSONObject();
        countsObject.put("available", 5);
        countsObject.put("out_of_service", 3);
        countsObject.put("reserved", 0);
        countsObject.put("timestamp", "2013-08-19T11:45:06.8120000-04:00");
        countsObject.put("total", 200);
        countsObject.put("vacant", 80);
        invalidZone.put("counts", countsObject);
        invalidZone.put("id", 3);
        invalidZone.put("name", "Level 3");

        //when
        exception.expect(JSONException.class);
        ParkingZone zone = new ParkingZone(invalidZone);

    }
}
