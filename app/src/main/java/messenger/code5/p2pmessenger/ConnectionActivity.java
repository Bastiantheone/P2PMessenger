package messenger.code5.p2pmessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sababado.circularview.CircularView;
import com.sababado.circularview.Marker;

public class ConnectionActivity extends AppCompatActivity {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private WifiP2pDevice[] p2pDevices;
    private LayoutInflater inflater;
    private ViewGroup parent;

    private CircularView circularView;
    private Button discoverButton;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent = (ViewGroup)findViewById(R.id.activity_connection);

        inflater.inflate(R.layout.circular_view_layout,parent);
        circularView = (CircularView)findViewById(R.id.circular_view);

        discoverButton = (Button)findViewById(R.id.discover_button);
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getBaseContext(),"Searching for Peers",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(getBaseContext(),"Discovery Failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        menuButton = (ImageButton)findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(ConnectionActivity.this,menuButton);
                menu.getMenuInflater().inflate(R.menu.connection_activity_menu,menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.create_group_option:
                                return true;
                            case R.id.join_group_option:
                                return true;
                            case R.id.settings_option:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                menu.show();
            }
        });
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void peersFound(WifiP2pDeviceList wifiP2pDeviceList){
        Log.d("TEST", "peersFound: "+wifiP2pDeviceList.getDeviceList().size());
        parent.removeView(circularView);
        inflater.inflate(R.layout.circular_view_layout,parent);
        CircularViewAdapter circularViewAdapter = new CircularViewAdapter(wifiP2pDeviceList.getDeviceList().size());
        circularView = (CircularView)findViewById(R.id.circular_view);
        circularView.setAdapter(circularViewAdapter);
        p2pDevices = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];
        wifiP2pDeviceList.getDeviceList().toArray(p2pDevices);
        circularView.setOnCircularViewObjectClickListener(new CircularView.OnClickListener() {
            @Override
            public void onClick(CircularView view) {
                Toast.makeText(getBaseContext(),"Clicked Center",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerClick(CircularView view, Marker marker, int position) {
                Toast.makeText(getBaseContext(),p2pDevices[position].deviceName,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
