package com.randiny_games.fingerprintauthenticator;

import org.jboss.aerogear.security.otp.Totp;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
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
                msg.put("identity", "Fingerprint Server");
                msg.put("version", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newFixedLengthResponse(msg.toString());
        } else if (uri.equals("/token")) {
            String secret = SecurePreferences.getStringValue("secretValue", "");

            if (secret.equals("")) {
                try {
                    msg.put("token","000000");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                Totp theTotp = new Totp(secret);
                String theToken;
                theToken = theTotp.now();
                try {
                    msg.put("token",theToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return newFixedLengthResponse(msg.toString());
        } else if (uri.equals("/store")){
            String secret = session.getParameters().get("secret").get(0);
            SecurePreferences.setValue("secretValue", secret);

            return newFixedLengthResponse("success");
        }
        return  null;
    }
}
