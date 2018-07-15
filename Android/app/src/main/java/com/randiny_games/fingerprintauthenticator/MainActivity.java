package com.randiny_games.fingerprintauthenticator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MyHTTPD server;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = (TextView)findViewById(R.id.statusText);
        statusText.setText(getString(R.string.starting));

        try {
            server = new MyHTTPD();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        statusText.setText(getString(R.string.started));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        mBuilder.setContentTitle("Fingerprint Authenticator");
        mBuilder.setContentText("Server is running");
        mBuilder.setOngoing(true);

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);

        notifManager.notify(1, mBuilder.build());



    }


}

