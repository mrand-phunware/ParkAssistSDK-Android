package com.phunware.parkassist;

import android.graphics.Bitmap;

import com.phunware.parkassist.models.ParkingZone;
import com.phunware.parkassist.models.PlateSearchResult;
import com.phunware.parkassist.networking.Callback;
import com.phunware.parkassist.networking.ParkAssistNetworkingInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by mrand on 3/30/16.
 */
public class ParkAssistUnitTests {

    private CountDownLatch latch;
    private ParkAssistNetworkingInterface testNetworkInterface;
    private ParkAssistSDK testSDK;

    @Before
    public void setUp() throws Exception {
        latch = new CountDownLatch(1);
        testNetworkInterface = Mockito.mock(ParkAssistNetworkingInterface.class);
        testSDK = new ParkAssistSDK("blah", "blah", testNetworkInterface);
    }

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
        invalidZone.put("id", 3);
        invalidZone.put("name", "Level 3");

        //when
        exception.expect(JSONException.class);
        ParkingZone zone = new ParkingZone(invalidZone);
    }

    @Test
    public void testNetworkSuccessCallsCallbackSuccess() throws Exception {
        //given
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface responseInterface =
                        (ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface)invocation
                                .getArguments()[1];
                responseInterface.onSuccess(new JSONArray());
                return null;
            }
        }).when(testNetworkInterface).getJSON(any(String.class), any(ParkAssistNetworkingInterface
                .ParkAssistJSONResponseInterface.class));
        //when
        testSDK.searchPlates("ZZZ", new Callback<List<PlateSearchResult>>() {
            @Override
            public void onSuccess(List<PlateSearchResult> data) {
                //then
                assertNotNull(data);
                latch.countDown();
            }

            @Override
            public void onFailed(Throwable e) {
                fail();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(latch.getCount(), 0);
    }

    @Test
    public void testLessThanThreeCharsFails() throws  Exception {
        //when
        testSDK.searchPlates("BB", new Callback<List<PlateSearchResult>>() {
            @Override
            public void onSuccess(List<PlateSearchResult> data) {
                fail();
            }

            @Override
            public void onFailed(Throwable e) {
                //then
                assertNotNull(e);
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(latch.getCount(), 0);
    }

    @Test
    public void testResponseFailureCallsCallbackFailure() throws Exception {
        //given
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface jsonInterface =
                        (ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface)invocation
                                .getArguments()[1];
                jsonInterface.onFailure(new Error("failboat"));
                return null;
            }
        })
                .when(testNetworkInterface).getJSON(any(String.class),
                any(ParkAssistNetworkingInterface.ParkAssistJSONResponseInterface.class));

        //when
        testSDK.searchPlates("BBB", new Callback<List<PlateSearchResult>>() {
            //then
            @Override
            public void onSuccess(List<PlateSearchResult> data) {
                fail();
            }

            @Override
            public void onFailed(Throwable e) {
                assertEquals(e.getMessage(), "failboat");
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    @Test
    public void testImageSuccess() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParkAssistNetworkingInterface.ParkAssistImageResponseInterface imageInterface = (ParkAssistNetworkingInterface.ParkAssistImageResponseInterface)invocation.getArguments()[1];
                imageInterface.onSuccess(new byte[3]);
                return null;
            }
        })
                .when(testNetworkInterface).getImage(anyString(), any(ParkAssistNetworkingInterface.ParkAssistImageResponseInterface.class));
        testSDK.getMapImage("map", new Callback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                latch.countDown();
            }

            @Override
            public void onFailed(Throwable e) {
                fail(e.getMessage());
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    @Test
    public void testImageFailure() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParkAssistNetworkingInterface.ParkAssistImageResponseInterface imageInterface = (ParkAssistNetworkingInterface.ParkAssistImageResponseInterface)invocation.getArguments()[1];
                imageInterface.onFailure(new Error("imageFail"));
                return null;
            }
        })
                .when(testNetworkInterface).getImage(anyString(), any(ParkAssistNetworkingInterface.ParkAssistImageResponseInterface.class));
        testSDK.getMapImage("map", new Callback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                fail();
            }

            @Override
            public void onFailed(Throwable e) {
                assertEquals(e.getMessage(), "imageFail");
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }
}
