package com.randiny_games.fingerprintauthenticator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class MyHTTPD extends NanoHTTPD {
    public static final int PORT = 1234;

    public MyHTTPD() throws IOException {
        super(PORT);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        JSONObject msg = new JSONObject();

        if (uri.equals("/identity")) {
            try {
                msg.put("identity","Fingerprint Server");
                msg.put("version",1);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return newFixedLengthResponse(msg.toString());
        }
        return  null;
    }
}
