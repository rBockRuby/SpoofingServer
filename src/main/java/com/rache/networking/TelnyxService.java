package com.rache.networking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.HashMap;

public interface TelnyxService {

    @POST("/messages")
    Call<String> sendMessage(
            @Header("x-profile-secret") String profileSecret,
            @Body HashMap<String, String> body);
}
