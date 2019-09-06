package com.rache.resources;

import com.rache.data.calls.CallData;
import com.rache.data.calls.CallRequest;
import com.rache.data.calls.Payload;
import com.rache.db.ConnectionPool;
import com.rache.networking.BasicAuthInterceptor;
import com.rache.networking.TelnyxService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

@Path("/call")
public class CallSpoofResource {
    private Retrofit retrofit;

    private static String TELNYX_API_SECRET = "Kcu6SKYARPCr1yReo9NczQ";
    private static String TELNYX_API_KEY = "c110f6e8-494b-44c7-a090-ef34e4c8f3be";

    private List<String> sessionList = new ArrayList<>();
    private Map<String, String> sessionMap = new HashMap<>();
    private boolean record = false;

    public CallSpoofResource() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(TELNYX_API_KEY, TELNYX_API_SECRET))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.telnyx.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }

    private static final String ADD_NEW_CALL = "INSERT INTO call_data (from_number, to_number, user_id)" +
            "VALUES (?, ?, ?)";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/initiate_call")
    public Map<String, String> initiateCall(CallRequest callRequest) {
        if (callRequest == null) {
            return new HashMap<String, String>();
        }

        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(ADD_NEW_CALL);
            stmt.setString(1, callRequest.getFromNumber());
            stmt.setString(2, callRequest.getToNumber());
            stmt.setString(3, callRequest.getUserId());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Map<String, String> response = new HashMap<String, String>();
        response.put("numberToCall", "+19712881007");
        response.put("pause", ",,");
        response.put("pin", "5546");

        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/cycid")
    public String handleIncomingCall(CallData callData) throws IOException {
        if (callData == null) {
            return "Failed to receive call data";
        }

        Payload payload = callData.getPayload();

        String controlId = "Unknown";
        if (payload.getCall_control_id() != null) {
            controlId = payload.getCall_control_id();
        }
        String clientState = "None";
        if (payload.getClient_state() != null) {
            clientState = payload.getClient_state();
        }

        if (callData.getEvent_type().equals("call_initiated")) {
            answerCall(controlId, payload.getCall_session_id());
        } else if (callData.getEvent_type().equals("recording_saved")) {
//            String urls = payload.getRecording_urls();
//            String pubUrls = payload.getPublic_recording_urls();
//            System.out.print(urls + " " + pubUrls);
        } else {
            String decodedClientState = decode(clientState);
            if (decodedClientState.equals("gather_digits")) {
                gatherDigits(controlId);
            } else if (decodedClientState.equals("evaluate_digits") && callData.getEvent_type().equals("gather_ended")) {
                String digits = payload.getDigits();
                evaluateDigits(controlId, digits);
            } else if (decodedClientState.equals("disconnect_call_auth_error") && callData.getEvent_type().equals("speak_ended")) {
                disconnectCall(controlId, payload.getCall_session_id());
            } else if (decodedClientState.equals("disconnect_call")) {
                disconnectCall(controlId, payload.getCall_session_id());
            } else if (decodedClientState.equals("play_legal") && callData.getEvent_type().equals("call_answered")) {
                String message = "This call may be recorded for quality assurance or training purposes.";
                clientState = encode("bridge_call");
                sayMessage(controlId, message, clientState);
            } else if (decodedClientState.equals("bridge_call") && callData.getEvent_type().equals("speak_ended")) {
                clientState = encode("record_call");
                bridgeCall(controlId, payload.getCall_session_id(), clientState);
            } else if (decodedClientState.equals("record_call") && callData.getEvent_type().equals("call_bridged")) {
                clientState = encode("");
                recordCall(controlId, payload.getCall_session_id(), clientState);
            }
        }

        return "Success";
    }

    private String sayMessage(String controlId, String message, String clientState) {
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("payload", "message");
        body.put("client_state", clientState);
        body.put("payload_type", "text");
        body.put("service_level", "premium");
        body.put("voice", "female");
        body.put("language", "en-US");

        Call<Map<String, Object>> call = telnyxService.sayMessage(body, controlId);

        try {
            Response response = call.execute();
            return response.message();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed sayMessage";
    }

    private void answerCall(String controlId, String session) throws IOException {
        System.out.print("Answer Call");
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);
        String clientState = "";

        if (sessionMap.containsKey(session)) {
            if (record) {
                // pass
            } else {
                clientState = encode("incoming_transfer");
                HashMap<String, String> body = new HashMap<>();
                body.put("client_state", clientState);

                Call<Map<String, Object>> call = telnyxService.answerCall(body, controlId);
                call.execute();
            }
        } else {
            clientState = encode("gather_digits");
            sessionMap.put(session, controlId);
            HashMap<String, String> body = new HashMap<>();
            body.put("client_state", clientState);

            Call<Map<String, Object>> call = telnyxService.answerCall(body, controlId);
            call.execute();
        }
    }

    private String disconnectCall(String controlId, String session) {
        System.out.print("Disconnect Call");

        TelnyxService telnyxService = retrofit.create(TelnyxService.class);
        sessionMap.remove(session);

        Call<Map<String, Object>> call = telnyxService.disconnectCall(TELNYX_API_KEY + ":" + TELNYX_API_SECRET, controlId);

        try {
            Response response = call.execute();
            return response.message();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed disconnectCall";
    }


    private String gatherDigits(String controlId) {
        System.out.print("Gather digits");

        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        String clientState = encode("evaluate_digits");
        HashMap<String, String> body = new HashMap<>();
        body.put("payload", "Please wait while we connect your call");
        body.put("payload_type", "text");
        body.put("service_level", "premium");
        body.put("voice", "female");
        body.put("language", "en-US");
        body.put("min", "1");
        body.put("max", "4");
        body.put("tries", "4");
        body.put("timeout", "8000");
        body.put("terminating_digit", "#");
        body.put("client_state", clientState);

        Call<Map<String, Object>> call = telnyxService.gatherDigits(body, controlId);
        try {
            Response response = call.execute();
            return response.message();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    private String transferCall(String controlId, String toNumber, String fromNumber) {
        System.out.print("Transfer Call");

        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        String clientState = encode("call_transfer");

        HashMap<String, String> body = new HashMap<>();
        body.put("to", toNumber);
        body.put("from", fromNumber);
        body.put("client_state", clientState);

        Call<Map<String, Object>> call = telnyxService.transferCall(body, controlId);

        try {
            Response response = call.execute();
            return response.message();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Failed transferCall";
    }

    private String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private String decode(String data) {
        byte[] bytes = Base64.getDecoder().decode(data);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static final String GET_CALL_TO_CONNECT = "SELECT * FROM call_data ORDER BY id DESC LIMIT 1";

    private void evaluateDigits(String controlId, String digits) {
        if (digits.equals("5546")) {
            System.out.println("Transferring call");

            CallRequest callRequest = new CallRequest();

            try (Connection conn = ConnectionPool.getConnection()) {
                Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(GET_CALL_TO_CONNECT);

                while (resultSet.next()) {
                    callRequest.setFromNumber(resultSet.getString("from_number"));
                    callRequest.setToNumber(resultSet.getString("to_number"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (callRequest.getFromNumber() != null && callRequest.getToNumber() != null) {
                if (record) {
                    placeCall(controlId, callRequest.getToNumber(), callRequest.getFromNumber());
                } else {
                    transferCall(controlId, callRequest.getToNumber(), callRequest.getFromNumber());
                }
            } else {
                System.out.println("something failed");
                String clientState = encode("disconnect_call_auth_error");
                String message = "Sorry something failed, please try again.  Goodbye!";
                sayMessage(controlId, message, clientState);
            }
        } else {
            System.out.println("authentication failed");
            String clientState = encode("disconnect_call_auth_error");
            String message = "Authentication failed, please try again.  Goodbye!";
            sayMessage(controlId, message, clientState);
        }
    }

    private void placeCall(String controlId, String toNumber, String fromNumber) {
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        String clientState = encode("play_legal");
        String connectionId = "1189227170199766935";

        HashMap<String, Object> body = new HashMap<>();
        body.put("to", toNumber);
        body.put("from", fromNumber);
        body.put("connection_id", connectionId);
        body.put("timeout", 30);
        body.put("client_state", clientState);
        body.put("link_to", controlId);

        Call<Map<String, Object>> call = telnyxService.placeCall(body);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bridgeCall(String controlId, String session, String clientState) {
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        String initialCallLeg = sessionMap.get(session);

        HashMap<String, String> body = new HashMap<>();
        body.put("call_control_id", initialCallLeg);
        body.put("client_state", clientState);

        Call<Map<String, Object>> call = telnyxService.bridgeCall(body, controlId);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordCall(String controlId, String session, String clientState) {
        TelnyxService telnyxService = retrofit.create(TelnyxService.class);

        String initialCallLeg = sessionMap.get(session);

        HashMap<String, Object> body = new HashMap<>();
        body.put("format", "mp3");
        body.put("channels", "dual");
        body.put("play_beep", true);

        Call<Map<String, Object>> call = telnyxService.recordCall(body, initialCallLeg);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
