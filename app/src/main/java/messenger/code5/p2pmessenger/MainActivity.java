package messenger.code5.p2pmessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    ArrayList<Message> messages;
    private WifiP2pGroup p2pGroup;
    private WifiP2pDevice myDevice;
    private boolean connectedAndReady;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    public Button sendButton;
    public EditText mText;
    public RecyclerView rvMessages;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //start the async task to receive messages
        ServerAsyncTask server = new ServerAsyncTask(this);

        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);

        // Initialize contacts
        messages = new ArrayList<>();

        // Set layout manager to position the items
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        // Create adapter passing in the sample user data
        MessageAdapter adapter = new MessageAdapter(this, messages);
        // Attach the adapter to the recyclerview to populate items
        rvMessages.setAdapter(adapter);

        sendButton = (Button) findViewById(R.id.sendButton);
        mText = (EditText) findViewById(R.id.typeMessage) ;
        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               String mesg = mText.getText().toString();
                if(connectedAndReady){
                    send(mesg);
                    addMessage(mesg);
                }else{
                    Toast.makeText(getBaseContext(),"Cannot currently send",Toast.LENGTH_SHORT).show();
                }
           }
        });
    }

    public void addMessage(String message){
        messages.add((new Message(message)));
        MessageAdapter adapter = new MessageAdapter(getBaseContext(), messages);
        rvMessages.setAdapter(adapter);
    }

    public void send(String message){
        String deviceAddress = p2pGroup.getOwner().deviceAddress;
        //maybe p2pGroup.getNetworkName
        Intent intent = new Intent(getBaseContext(),TransferMessageService.class);
        intent.setAction(TransferMessageService.ACTION_SEND_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putString(TransferMessageService.EXTRAS_MESSAGE,message);
        bundle.putString(TransferMessageService.EXTRAS_ADDRESS,deviceAddress);
        bundle.putInt(TransferMessageService.EXTRAS_PORT,8888);
        intent.putExtras(bundle);
        startService(intent);
    }

    public void setNetworkToReadyState(boolean status, WifiP2pGroup group, WifiP2pDevice device){
        p2pGroup = group;
        myDevice = device;
        connectedAndReady = status;
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
}
