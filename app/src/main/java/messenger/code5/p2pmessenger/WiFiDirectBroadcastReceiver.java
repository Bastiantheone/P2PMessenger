package messenger.code5.p2pmessenger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Activity activity;

    Button discoverPeersButton;
    TextView availablePeersTextView1;
    TextView availablePeersTextView2;
    TextView availablePeersTextView3;
    TextView availablePeersTextView4;
    TextView statusTextView;
    Button cancelButton;

    public WiFiDirectBroadcastReceiver(final WifiP2pManager manager, final WifiP2pManager.Channel channel,
                                       final Activity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;

        availablePeersTextView1 = (TextView)activity.findViewById(R.id.available_peers_text_view1);
        availablePeersTextView2 = (TextView)activity.findViewById(R.id.available_peers_text_view2);
        availablePeersTextView3 = (TextView)activity.findViewById(R.id.available_peers_text_view3);
        availablePeersTextView4 = (TextView)activity.findViewById(R.id.available_peers_text_view4);
        statusTextView = (TextView)activity.findViewById(R.id.status_text_view);
        cancelButton = (Button)activity.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(activity,"Success",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(activity,"Failure",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        discoverPeersButton = (Button)activity.findViewById(R.id.discover_peers_button);
        discoverPeersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                statusTextView.setText(R.string.wifip2p_enabled);
            } else {
                // Wi-Fi P2P is not enabled
                statusTextView.setText(R.string.wifip2p_not_enabled);
            }
        }
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        //display a list with all the available devices
                        String deviceNameList = "Available Peers: \n";
                        int size = wifiP2pDeviceList.getDeviceList().size();
                        int counter = 0;
                        for(final WifiP2pDevice device:wifiP2pDeviceList.getDeviceList()){
                            if(size>0&&counter==0){
                                availablePeersTextView1.setText(device.deviceName);
                                availablePeersTextView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        connectPeer(device);
                                    }
                                });
                                counter++;
                            }
                            else if(size>1&&counter==1){
                                availablePeersTextView2.setText(device.deviceName);
                                availablePeersTextView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        connectPeer(device);
                                    }
                                });
                                counter++;
                            }

                        }
                    }
                });
            }
        }

    }



    public void connectPeer(WifiP2pDevice device){
        //obtain a peer from the WifiP2pDeviceList
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
                availablePeersTextView3.setText("connect success");
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
                availablePeersTextView3.setText("connect failure");
            }
        });
    }

    public void discoverPeers(){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                availablePeersTextView4.setText("Discover in progress");
            }

            @Override
            public void onFailure(int reasonCode) {
                availablePeersTextView4.setText("Discovery failed");
            }
        });
    }

}
