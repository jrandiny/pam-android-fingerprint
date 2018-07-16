package com.randiny_games.fingerprintauthenticator;

import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener{

    private Button authButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        authButton = (Button)findViewById(R.id.authButton);

        authButton.setOnClickListener(this);



    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("masuk");



    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.authButton){
            synchronized (MyHTTPD.syncToken){
                MyHTTPD.syncToken.notify();
            }
        }
    }
    