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
    public static final int PORT = 1234;
    private Context context;
    private String testString = "test";

    public MyHTTPD(Context context) throws IOException {
        super(PORT);
        this.context = context;
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

            Intent intent = new Intent(context, AuthActivity.class);

            context.startActivity(intent);

            synchronized(syncToken){
                try{
                    System.out.println("Waiting for b to complete...");
                    syncToken.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("test");
            }

            String secret = SecurePreferences.getStringValue("decryptedKey", "");

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

                SecurePreferences.removeValue("decryptedKey");
            }


            return newFixedLengthResponse(msg.toString());
        } else if (uri.equals("/store")){

            String secret = session.getParameters().get("secret").get(0);
            if(!secret.equals("")){
                SecurePreferences.setValue("decryptedKey", secret);

                Intent intent = new Intent(context, setupActivity.class);

                context.startActivity(intent);

                synchronized(syncToken){
                    try{
                        System.out.println("Waiting for setup to complete...");
                        syncToken.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    System.out.println("test2");
                }

                return newFixedLengthResponse("success");
            }else {
                return newFixedLengthResponse("error");
            }

        }
        return  null;
    }
}
