package com.randiny_games.fingerprintauthenticator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusm.kfingerprintmanager.KFingerprintManager;

import org.jetbrains.annotations.NotNull;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

public class setupActivity extends AppCompatActivity implements View.OnClickListener {

    private KFingerprintManager fm;
    private TextView status;
    private String secret;
    private Button retryBtn;

    private Boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        status = (TextView) findViewById(R.id.setupStatus);
        retryBtn = (Button) findViewById(R.id.setupRetryBtn);

        retryBtn.setOnClickListener(this);
        retryBtn.setEnabled(false);

        fm = new KFingerprintManager(this,"fingerprintPam");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();

        registered = false;

        if(isScreenOn){
            attemptEncrypt();
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
            attemptEncrypt();

            unregisterReceiver(screenCheck);
            registered = false;
        }
    };

    private void attemptEncrypt(){

        secret = SecurePreferences.getStringValue("decryptedKey","");

        fm.encrypt(secret, new KFingerprintManager.EncryptionCallback() {
            @Override
            public void onEncryptionSuccess(@NotNull String messageEncrypted) {
                secret = null;
                SecurePreferences.removeValue("decryptedKey");
                SecurePreferences.setValue("secret", messageEncrypted);
                returnToServer();
            }

            @Override
            public void onEncryptionFailed() {
                status.setText("Encryption failed");
                failFatal();
            }

            @Override
            public void onFingerprintNotRecognized() {
                status.setText("Fingerprint not recognized");
                retryBtn.setEnabled(true);
            }

            @Override
            public void onAuthenticationFailedWithHelp(@Nullable String help) {
                status.setText(help);
                retryBtn.setEnabled(true);
            }

            @Override
            public void onFingerprintNotAvailable() {
                status.setText("Fingerprint not available");
                failFatal();
            }

            @Override
            public void onCancelled() {
                status.setText("Operation cancelled by user");
                retryBtn.setEnabled(true);
            }
        }, getSupportFragmentManager());

    }

    private void returnToServer(){

        synchronized (MyHTTPD.syncToken) {
            MyHTTPD.syncToken.notify();
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setupRetryBtn){
            attemptEncrypt();
        }
    }

    private void failFatal(){
        Toast.makeText(this,"Fatal error",Toast.LENGTH_LONG);
        returnToServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(registered){
            unregisterReceiver(screenCheck);
        }

        returnToServer();
    }
}
