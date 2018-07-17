package com.randiny_games.fingerprintauthenticator;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView statusText;
    private String ip;
    private FloatingActionButton fab;
    private Intent serverIntent;
    private Button setupButton;

    private Boolean serverStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = (TextView)findViewById(R.id.statusText);
        fab = (FloatingActionButton) findViewById(R.id.startStopFab);
        setupButton = (Button) findViewById(R.id.setupBtn);

        serverIntent = new Intent(this, server.class);

        if(savedInstanceState!=null){
            serverStatus = savedInstanceState.getBoolean("serverStatus");
            if(!serverStatus){
                startServer();
            }
        }else {
            startServer();
        }

        fab.setOnClickListener(this);
        setupButton.setOnClickListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("serverStatus",serverStatus);
        super.onSaveInstanceState(outState, outPersistentState);

    }

    private void startServer(){
        statusText.setText(getString(R.string.statusStarting));

        startService(serverIntent);
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop_white_24dp));
        serverStatus = true;

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        statusText.setText(getString(R.string.statusStarted) + ip);
    }

    private void stopServer(){
        stopService(serverIntent);
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white_24dp));
        serverStatus = false;

        statusText.setText(R.string.statusStopped);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.startStopFab){
            if(serverStatus){
                stopServer();
            }else {
                startServer();
            }

        }
    }
}

