package com.randiny_games.fingerprintauthenticator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MyHTTPD server;
    private TextView statusText;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = (TextView)findViewById(R.id.statusText);
        statusText.setText(getString(R.string.starting));

        Intent intent = new Intent(this, server.class);
        startService(intent);

        statusText.setText(getString(R.string.started));

        findViewById(R.id.setupBtn).setOnClickListener(this);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        statusText.setText(getString(R.string.started) + " on " + ip);

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.setupBtn){

        }
    }
}

