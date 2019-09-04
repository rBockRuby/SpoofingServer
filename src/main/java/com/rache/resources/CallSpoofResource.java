package com.rache.resources;

import com.rache.networking.TelnyxService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.POST;

import javax.ws.rs.Path;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@Path("/call")
public class CallSpoofResource {
    private Retrofit retrofit;

    private static String TELNYX_API_TOKEN = System.getenv("TELNYX_API_TOKEN");
    private static String TELNYX_API_KEY = System.getenv("TELNYX_API_KEY");

    public CallSpoofResource() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://sms.telnyx.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @POST("/incomingCall")
    public String handleIncomingCall() {
//        incomingcall = request.get_json()
//        event_type = incomingcall['event_type']
//        payload = incomingcall['payload']
//        session = payload['call_session_id']
//        try:
//        control_id = payload['call_control_id']
//        except:
//        control_id = 'Unknown'
//        try:
//        client_state = payload['client_state']
//        except:
//        client_state = None
//    # logout = 'Event Type: {}\nCall Control ID: {}\nClient State: {}\nSession: {}'.format(event_type,control_id,client_state,session)
//    # print(logout)
//        if event_type == 'call_initiated':
//        control_id = payload['call_control_id']
//        answer_call(control_id, session)
//    else:
//        dclient_state = decode(client_state)
//​
//        if dclient_state == 'gather_digits':
//        control_id = payload['call_control_id']
//        gather_digits(control_id)
//        elif dclient_state == 'evaluate_digits' and event_type == 'gather_ended':
//        control_id = payload['call_control_id']
//        digits = payload['digits']
//        evaluate_digits(control_id, digits)
//        elif dclient_state == 'disconnect_call_auth_error' and event_type == 'speak_ended':
//        control_id = payload['call_control_id']
//        disconnect_call(control_id, session)
//        elif dclient_state == 'disconnect_call':
//        control_id = payload['call_control_id']
//        disconnect_call(control_id, session)
//        else:
//        return '', 200
//        return '', 200

        return "Success";
    }

    private String gatherDigits(String controlId) {
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

        Call<String> call = telnyxService.gatherDigits(TELNYX_API_KEY + ":" + TELNYX_API_TOKEN, body, controlId);
        try {
            Response response = call.execute();
            return response.message();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    private String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private void evaluateDigits(String controlId, String digits) {
        if (digits.equals("5546")) {
            System.out.println("Transfering call");
            // TODO: CALL TRANSFER CALL
        } else {
            System.out.println("authentication failed");
            //TODO: CALL SAY MESSAGE
        }
    }
}
