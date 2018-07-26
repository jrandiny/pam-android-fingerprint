package com.randiny_games.fingerprintauthenticator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusm.kfingerprintmanager.KFingerprintManager;

import org.jetbrains.annotations.NotNull;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private Button authButton;
    private KFingerprintManager fm;
    private TextView status;

    private Boolean registered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        authButton = (Button) findViewById(R.id.authButton);
        status = (TextView) findViewById(R.id.authDesc);

        authButton.setOnClickListener(this);
        authButton.setEnabled(false);

        fm = new KFingerprintManager(this,"fingerprintPam");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();

        registered = false;

        if(isScreenOn){
            attemptDecrypt();
        }else {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(screenCheck, filter);
            registered = true;
        }


    }

    private final BroadcastReceiver screenCheck = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            attemptDecrypt();

            unregisterReceiver(screenCheck);
            registered = false;
        }
    };

    private void attemptDecrypt(){

        String encData = SecurePreferences.getStringValue("secret","");

        SecurePreferences.removeValue("decryptedKey");

        if(encData.equals("")){
            failAuth();
        }

        fm.decrypt(encData, new KFingerprintManager.DecryptionCallback() {
            @Override
            public void onDecryptionSuccess(@NotNull String messageDecrypted) {
                SecurePreferences.setValue("decryptedKey",messageDecrypted);
                returnToServer();
            }

            @Override
            public void onDecryptionFailed() {
                status.setText("Decryption failed");
                failAuth();
            }

            @Override
            public void onFingerprintNotRecognized() {
                status.setText("Fingerprint not recognized");
                authButton.setEnabled(true);
            }

            @Override
            public void onAuthenticationFailedWithHelp(@Nullable String help) {
                status.setText(help);
                authButton.setEnabled(true);
            }

            @Override
            public void onFingerprintNotAvailable() {
                status.setText("Fingerprint not available");
                failAuth();
            }

            @Override
            public void onCancelled() {
                status.setText("Cancelled");
                authButton.setEnabled(true);
            }
        }, getSupportFragmentManager());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.authButton) {
            attemptDecrypt();
        }
    }

    private void returnToServer(){

        synchronized (MyHTTPD.syncToken) {
            MyHTTPD.syncToken.notify();
        }
        finish();
    }

    private void failAuth(){
        Toast.makeText(this,"Fail to get encrypted data, have you run setup?",Toast.LENGTH_LONG).show();
        returnToServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(registered){
            unregisterReceiver(screenCheck);
            registered = false;
        }

    }
}
