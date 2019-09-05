package com.rache.resources;

import com.rache.data.texts.TelnyxText;
import com.rache.data.texts.TelnyxTextRequest;
import com.rache.data.texts.TextRequest;
import com.rache.db.ConnectionPool;
import com.rache.networking.TelnyxService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("/text")
public class TextSpoofResource {
    private Retrofit retrofit;

    private static final String ADD_NEW_TEXT = "INSERT INTO text_data (from_number, to_number, body, is_incoming)" +
            "VALUES (?, ?, ?, ?)";

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

        // Write to db
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(ADD_NEW_TEXT);
            stmt.setString(1, textRequest.getFromNumber());
            stmt.setString(2, textRequest.getToNumber());
            stmt.setString(3, textRequest.getMessageBody());
            stmt.setBoolean(4, false);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Send to telnyx
        try {
            HashMap<String, String> body = new HashMap<String, String>();
            body.put("from", textRequest.getFromNumber());
            body.put("to", textRequest.getToNumber());
            body.put("body", textRequest.getMessageBody());

            Call<Map<String, Object>> call = telnyxService.sendMessage("Vy0M3eCysbDVsSFJncOvkyqq", body);
            Response<Map<String, Object>> response = call.execute();
            return response.message();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failure";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/incoming_sms")
    public String receiveText(TelnyxText telnyxText) {
        if (telnyxText == null) {
            return "Failed - no text data";
        }

        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(ADD_NEW_TEXT);
            stmt.setString(1, telnyxText.getFrom());
            stmt.setString(2, telnyxText.getTo());
            stmt.setString(3, telnyxText.getBody());
            stmt.setBoolean(4, true);

            stmt.execute();
            return "Success";
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Failed for some other reason";
    }
}
