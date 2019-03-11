package com.leesc.tazza.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.leesc.tazza.service.WifiService;

import dagger.android.AndroidInjection;


public class WifiDirectReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiService wifiService;
    private WifiP2pDevice wifiP2pDevice;

    public WifiDirectReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, WifiService wifiService) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.wifiService = wifiService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
//        Intent serviceIntent = new Intent(context, WifiService.class);
//        serviceIntent.setAction(intent.getAction());
//        serviceIntent.putExtras(intent);
//        context.startService(serviceIntent);

        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:

                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d("lsc", "WifiDirectReceiver wifi enable");
                } else {
                    Log.d("lsc", "WifiDirectReceiver wifi disable");
                }
                break;

            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                Log.d("lsc", "WifiDirectReceiver WIFI_P2P_PEERS_CHANGED_ACTION");
                if (wifiP2pManager != null) {
                    wifiP2pManager.requestPeers(channel, wifiService);
                }
                break;

            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                if (wifiP2pManager == null) {
                    return;
                }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                Log.d("lsc", "WifiDirectReceiver networkInfo " + networkInfo.isConnected());
                if (networkInfo.isConnected()) {
                    wifiP2pManager.requestConnectionInfo(channel, wifiService);
                } else {
                    Log.d("lsc", "WifiDirectReceiver disconnect");
                }
                break;

            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                break;

        }

    }
}
