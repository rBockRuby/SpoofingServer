package com.rache.networking;

import com.fasterxml.jackson.databind.util.JSONPObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import javax.ws.rs.PathParam;
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
    Call<String> sayMessage(
            @Header("Authorization") String auth,
            @Body HashMap<String, String> body,
            @PathParam("controlId") String controlId);

    @POST("/calls/{controlId}/actions/answer")
    Call<String> answerCall(@Header("Authorization") String auth,
                            @Body HashMap<String, String> body,
                            @PathParam("controlId") String controlId);

    @POST("/calls/{controlId}/actions/hangup")
    Call<String> disconnectCall(@Header("Authorization") String auth,
                                @PathParam("controlId") String controlId);

    @POST("/calls/{controlId}/actions/gather_using_speak")
    Call<String> gatherDigits(@Header("Authorization") String auth,
                              @Body HashMap<String, String> body,
                              @PathParam("controlId") String controlId);

    @POST("/calls/{controlId}/actions/transfer")
    Call<String> transferCall(@Header("Authorization") String auth,
                              @Body HashMap<String, String> body,
                              @PathParam("controlId") String controlId);


}
