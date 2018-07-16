package com.randiny_games.fingerprintauthenticator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        authButton = (Button) findViewById(R.id.authButton);
        status = (TextView) findViewById(R.id.authDesc);

        authButton.setOnClickListener(this);

        fm = new KFingerprintManager(this,"fingerprintPam");



    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("masuk");

    }

    private void attemptDecrypt(){

        String encData = SecurePreferences.getStringValue("secret","");

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
                failAuth();
            }

            @Override
            public void onCancelled() {
                status.setText("Cancelled");
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
    }

    private void failAuth(){
        Toast.makeText(this,"FAILL",Toast.LENGTH_LONG);
    }
}
