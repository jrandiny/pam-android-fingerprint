package com.randiny_games.fingerprintauthenticator;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jesusm.kfingerprintmanager.KFingerprintManager;

import org.jetbrains.annotations.NotNull;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

public class setupActivity extends AppCompatActivity {

    private KFingerprintManager fm;
    private TextView status;
    private String secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        status = (TextView) findViewById(R.id.setupStatus);

        fm = new KFingerprintManager(this,"fingerprintPam");

    }

    @Override
    protected void onStart() {
        super.onStart();

        encryptData();
    }

    private void encryptData(){

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
            }

            @Override
            public void onFingerprintNotRecognized() {
                status.setText("Fingerprint not recognized");
            }

            @Override
            public void onAuthenticationFailedWithHelp(@Nullable String help) {
                status.setText(help);
            }

            @Override
            public void onFingerprintNotAvailable() {
                status.setText("Fingerprint not available");
            }

            @Override
            public void onCancelled() {
                status.setText("Operation cancelled by user");
            }
        }, getSupportFragmentManager());

    }

    @Override
    protected void onPause() {
        super.onPause();

        returnToServer();
    }

    private void returnToServer(){

        synchronized (MyHTTPD.syncToken) {
            MyHTTPD.syncToken.notify();
        }
        finish();
    }
}
