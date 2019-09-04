package com.rache.resources;

import java.util.Base64;

public class CallSpoofResource {

    private static String TELNYX_API_TOKEN = System.getenv("TELNYX_API_TOKEN");
    private static String TELNYX_API_KEY = System.getenv("TELNYX_API_KEY");

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
