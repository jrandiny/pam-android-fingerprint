package com.randiny_games.fingerprintauthenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.IOException;

public class server extends Service {
    private MyHTTPD httpServer;

    public server() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            httpServer = new MyHTTPD();
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        mBuilder.setContentTitle("Fingerprint Authenticator");
        mBuilder.setContentText("Server is running");
        mBuilder.setOngoing(true);

        startForeground(1,mBuilder.build());

        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        httpServer.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
