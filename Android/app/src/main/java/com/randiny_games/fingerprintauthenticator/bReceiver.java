package com.randiny_games.fingerprintauthenticator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class bReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            context.startActivity(mainActivityIntent);
        }
    }
}
