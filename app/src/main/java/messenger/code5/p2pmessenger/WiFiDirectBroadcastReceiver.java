package messenger.code5.p2pmessenger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{
    private static final String TAG = "BroadcastReceiver";

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ConnectionActivity connectionActivity;
    private JoinGroupActivity joinGroupActivity;
    private MainActivity mainActivity;
    private boolean isConnectionActivity;
    private boolean isMainActivity;

    public WiFiDirectBroadcastReceiver(final WifiP2pManager manager, final WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = activity;
        isMainActivity=true;
    }

    public WiFiDirectBroadcastReceiver(final WifiP2pManager manager, final WifiP2pManager.Channel channel, ConnectionActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.connectionActivity = activity;
        isConnectionActivity = true;
        isMainActivity=false;
    }

    public WiFiDirectBroadcastReceiver(final WifiP2pManager manager, final WifiP2pManager.Channel channel, JoinGroupActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.joinGroupActivity = activity;
        isConnectionActivity = false;
        isMainActivity=false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if(ConnectionActivity.myDevice==null)ConnectionActivity.myDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        final String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Activity activity;
            if(isMainActivity)activity = mainActivity;
            else if(isConnectionActivity)activity = connectionActivity;
            else activity = joinGroupActivity;
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Toast.makeText(activity.getBaseContext(),"Wifi P2P is enabled",Toast.LENGTH_SHORT).show();
            } else {
                // Wi-Fi P2P is not enabled
                Toast.makeText(activity.getBaseContext(),"Wifi P2P is not enabled",Toast.LENGTH_SHORT).show();
            }
        }
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null && !isMainActivity) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        Log.d(TAG, "onPeersAvailable: ");
                        if(isConnectionActivity)connectionActivity.peersFound(wifiP2pDeviceList);
                        else joinGroupActivity.peersFound(wifiP2pDeviceList);
                    }
                });
            }
        }
        if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pGroup wifiGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

            if(networkState.isConnected()){
                mainActivity.setNetworkToReadyState(true,wifiGroup,device);
            }
        }
    }
}
