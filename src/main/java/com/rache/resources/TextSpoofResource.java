package com.rache.resources;


import com.rache.networking.TelnyxService;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Path("/text")
public class TextSpoofResource {
    private Retrofit retrofit;

    private static String TELNYX_SECRET = System.getenv("TELNYX_MSG_SECRET");

    public TextSpoofResource() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://sms.telnyx.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/test")
    public String testTexting() {
        return "Test succeeded";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/send_text")
    public String sendText() {
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("from", "+19712881015");
        body.put("to", "+14132817907");
        body.put("body", "Test text from innovation fest server");

        Call<String> call = telnyxService.sendMessage(TELNYX_SECRET, body);
        call.enqueue(new Callback<String>() {
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("Success");
            }

            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("Failure");
            }
        });

        return "Success";
    }


}
