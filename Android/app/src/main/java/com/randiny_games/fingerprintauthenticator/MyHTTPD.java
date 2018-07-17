package com.randiny_games.fingerprintauthenticator;

import android.content.Context;
import android.content.Intent;

import org.jboss.aerogear.security.otp.Totp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import fi.iki.elonen.NanoHTTPD;

public class MyHTTPD extends NanoHTTPD {
    public static Object syncToken = new Object();
    public static int port;
    private Context context;
    private String testString = "test";

    public MyHTTPD(Context context,Integer port) throws IOException {
        super(port);
        this.port = port;
        this.context = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        JSONObject msg = new JSONObject();

        // Return the identity of the server for verification purpose
        if (uri.equals("/identity")) {
            try {
                msg.put("identity", "Fingerprint Server");
                msg.put("version", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newFixedLengthResponse(msg.toString());

        }
        // Return authentication token
        else if (uri.equals("/token")) {

            // Call auth activity
            Intent intent = new Intent(context, AuthActivity.class);
            context.startActivity(intent);

            // Pause until auth activity finished
            synchronized(syncToken){
                try{
                    syncToken.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }

            // Get decrypted key
            String secret = SecurePreferences.getStringValue("decryptedKey", "");

            // Check if there's problem
            if (secret.equals("")) {
                try {
                    msg.put("token","000000");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                // Create token
                Totp theTotp = new Totp(secret);
                String theToken;
                theToken = theTotp.now();

                // Setting up response message
                try {
                    msg.put("token",theToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Clear decrypted key
                SecurePreferences.removeValue("decryptedKey");
            }

            return newFixedLengthResponse(msg.toString());
        }
        // Store secret for setup
        else if (uri.equals("/store")){

            // Get parameter
            String secret = session.getParameters().get("secret").get(0);

            // Check if secret received successfully
            if(!secret.equals("")){
                SecurePreferences.setValue("decryptedKey", secret);

                // Launching setup activity
                Intent intent = new Intent(context, setupActivity.class);
                context.startActivity(intent);

                // Wait for finish
                synchronized(syncToken){
                    try{
                        syncToken.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }

                return newFixedLengthResponse("success");
            }else {
                return newFixedLengthResponse("error");
            }

        }
        return  null;
    }
}
