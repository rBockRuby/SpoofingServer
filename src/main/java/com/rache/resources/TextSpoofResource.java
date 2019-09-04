package com.rache.resources;

import com.rache.data.texts.TelnyxTextRequest;
import com.rache.data.texts.TextRequest;
import com.rache.networking.TelnyxService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/text")
public class TextSpoofResource {
    private Retrofit retrofit;

    private static String TELNYX_SECRET = System.getenv("TELNYX_MSG_SECRET");
    private static String TELNYX_API_TOKEN = System.getenv("TELNYX_API_TOKEN");
    private static String TELNYX_API_KEY = System.getenv("TELNYX_API_KEY");

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
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/send_text")
    public String sendText(TextRequest textRequest) {
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        try {
            HashMap<String, String> body = new HashMap<String, String>();
            body.put("from", textRequest.getFromNumber());
            body.put("to", textRequest.getToNumber());
            body.put("body", textRequest.getMessageBody());

            Call<Map<String, Object>> call = telnyxService.sendMessage(TELNYX_SECRET, body);
            Response<Map<String, Object>> response = call.execute();
            return response.message();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failure";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String receiveText() {
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        try {
            Call<Map<String, Object>> call = telnyxService.receiveMessage();
            Response<Map<String, Object>> response = call.execute();
            return response.message();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failure";
    }
}
