package com.rache.networking;

import retrofit2.Call;
import retrofit2.http.*;

import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

public interface TelnyxService {

    // TEXT
    @POST("/messages")
    Call<Map<String, Object>> sendMessage(
            @Header("x-profile-secret") String profileSecret,
            @Body HashMap<String, String> body);

    @POST("/webhook")
    Call<Map<String, Object>> receiveMessage(
    );

    // CALL
    @POST("/calls/{controlId}/ations/speak")
    Call<Map<String, Object>> sayMessage(
            @Body HashMap<String, String> body,
            @Path("controlId") String controlId);

    @POST("/calls/{controlId}/actions/answer")
    Call<Map<String, Object>> answerCall(@Body HashMap<String, String> body,
                                         @Path("controlId") String controlId);

    @POST("/calls/{controlId}/actions/hangup")
    Call<Map<String, Object>> disconnectCall(@Header("Authorization") String auth,
                                             @Path("controlId") String controlId);

    @POST("/calls/{controlId}/actions/gather_using_speak")
    Call<Map<String, Object>> gatherDigits(
            @Body HashMap<String, String> body,
            @Path("controlId") String controlId);

    @POST("/calls/{controlId}/actions/transfer")
    Call<Map<String, Object>> transferCall(
            @Body HashMap<String, String> body,
            @Path("controlId") String controlId);

    @POST("/calls/{controlId}/actions/bridge")
    Call<Map<String, Object>> bridgeCall(
            @Body HashMap<String, String> body,
            @Path("controlId") String controlId
    );

    @POST("/calls/{initialCallLeg}/actions/record_start")
    Call<Map<String, Object>> recordCall(@Body HashMap<String, Object> body,
                                         @Path("initialCallLeg") String initialCallLeg);

    @POST("/calls")
    Call<Map<String, Object>> placeCall(@Body HashMap<String, Object> body);


}
