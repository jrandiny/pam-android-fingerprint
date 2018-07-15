package com.randiny_games.fingerprintauthenticator;

import org.jboss.aerogear.security.otp.Totp;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class MyHTTPD extends NanoHTTPD {
    public static final int PORT = 1234;

    private Totp theTotp = new Totp("testSecret");

    public MyHTTPD() throws IOException {
        super(PORT);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        JSONObject msg = new JSONObject();

        if (uri.equals("/identity")) {
            try {
                msg.put("identity", "Fingerprint Server");
                msg.put("version", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newFixedLengthResponse(msg.toString());
        } else if (uri.equals("/token")) {
            String theToken;
            theToken = theTotp.now();
            try {
                msg.put("token",theToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newFixedLengthResponse(msg.toString());
        }
        return  null;
    }
}
