package com.randiny_games.fingerprintauthenticator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;

import java.io.IOException;

public class server extends Service {
    private MyHTTPD httpServer;
    private Integer port;

    public server() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra("port")){
            port  = intent.getIntExtra("port",1234);
        }else{
            port = 1234;
        }


        try {
            httpServer = new MyHTTPD(this, port);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        startForeground(1,getMyNotif(ip, port));

        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiChanged, wifiFilter);

        return Service.START_STICKY;

    }

    private Notification getMyNotif(String ip,int port){
        PendingIntent mainIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.ic_fingerprint_white_24dp);
        mBuilder.setContentTitle("Fingerprint Authenticator");
        mBuilder.setContentText("Server on " + ip + "(port : " + port +")");
        mBuilder.setOngoing(true);
        mBuilder.setContentIntent(mainIntent);
        mBuilder.setPriority(Notification.PRIORITY_MIN);

        return mBuilder.build();
    }

    private final BroadcastReceiver wifiChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (nwInfo.isConnected()){
                WifiInfo wfInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.notify(1, getMyNotif(Formatter.formatIpAddress(wfInfo.getIpAddress()),port));

            }
        }
    };

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
