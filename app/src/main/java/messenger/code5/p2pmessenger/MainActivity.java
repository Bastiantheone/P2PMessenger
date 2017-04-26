package messenger.code5.p2pmessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    public static final String FLAG_SEND_ADDRESS = "a";
    public static final String FLAG_SEND_MESSAGE = "m";
    public static final String FLAG_SEND_ID = "i";
    ArrayList<Message> messages;
    private WifiP2pGroup p2pGroup;
    private WifiP2pDevice myDevice;
    private boolean connectedAndReady;
    private ServerAsyncTask server;
    private ArrayList<InetAddress>clientAddresses;
    public static boolean justJoined;
    public static int id;

    private WifiP2pInfo mInfo;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    public Button sendButton;
    private ImageButton menuButton;
    public EditText mText;
    public RecyclerView rvMessages;
    private MessageAdapter adapter;

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

        clientAddresses = new ArrayList<>();

        //start the async task to receive messages
        server = new ServerAsyncTask(this);
        server.execute();

        TextView title = (TextView)findViewById(R.id.title_text_view);
        String s = "P2PMessenger - Chat";
        title.setText(s);

        menuButton = (ImageButton)findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              PopupMenu menu = new PopupMenu(MainActivity.this, menuButton);
                                              menu.getMenuInflater().inflate(R.menu.activity_menu, menu.getMenu());

                                              menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                  @Override
                                                  public boolean onMenuItemClick(MenuItem item) {
                                                      switch (item.getItemId()) {
                                                          case R.id.create_group_option:
                                                              if (myDevice != null && !myDevice.isGroupOwner()) {
                                                                  mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                                                                      @Override
                                                                      public void onSuccess() {
                                                                          Log.d(TAG, "CreateGroup onSuccess: ");
                                                                          Toast.makeText(getBaseContext(), "Created Group", Toast.LENGTH_SHORT).show();
                                                                          Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                                          startActivity(intent);
                                                                      }

                                                                      @Override
                                                                      public void onFailure(int reason) {
                                                                          Log.d(TAG, "CreateGroup onFailure: " + reason);
                                                                      }
                                                                  });
                                                              }
                                                              return true;
                                                          case R.id.join_group_option:
                                                              Intent intent = new Intent(getBaseContext(), JoinGroupActivity.class);
                                                              startActivity(intent);
                                                              return true;
                                                          case R.id.show_peers_option:
                                                              Intent intentp = new Intent(getBaseContext(), ConnectionActivity.class);
                                                              startActivity(intentp);
                                                              return true;
                                                          case R.id.remove_group_option:
                                                              mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                                                                  @Override
                                                                  public void onSuccess() {
                                                                      Log.d(TAG, "RemoveGroup onSuccess: ");
                                                                      Toast.makeText(getBaseContext(), "Removed Group", Toast.LENGTH_SHORT).show();
                                                                  }

                                                                  @Override
                                                                  public void onFailure(int reason) {
                                                                      Log.d(TAG, "RemoveGroup onFailure: " + reason);
                                                                  }
                                                              });
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

        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);

        // Initialize contacts
        messages = new ArrayList<>();

        // Set layout manager to position the items
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        // Create adapter passing in the sample user data


        sendButton = (Button) findViewById(R.id.sendButton);
        mText = (EditText) findViewById(R.id.typeMessage) ;
        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               String mesg = mText.getText().toString();
                mText.setText("");
                if(connectedAndReady){
                    String message = FLAG_SEND_MESSAGE +id+FLAG_SEND_MESSAGE+ mesg;
                    if(mInfo.isGroupOwner){
                        addMessage(mesg,id);
                        sendToAll(message);
                    }
                    else sendToOwner(message);
                }else{
                    Toast.makeText(getBaseContext(),"Cannot currently send",Toast.LENGTH_SHORT).show();
                }
           }
        });
        adapter = new MessageAdapter(getBaseContext(), messages);
        rvMessages.setAdapter(adapter);
    }

    public void addMessage(String message, int mID){
        Log.d(TAG, "addMessage: ");
        Message m = new Message(message,mID);
        messages.add(m);
        adapter.notifyItemInserted(messages.size()-1);
        rvMessages.smoothScrollToPosition(messages.size()-1);
    }

    public void receiveMessage(String message, int mID){
        addMessage(message, mID);

        if(mInfo.isGroupOwner)sendToAll(FLAG_SEND_MESSAGE+mID+FLAG_SEND_MESSAGE+message);

        //start the async task to receive messages
        server = new ServerAsyncTask(this);
        server.execute();
    }

    public void sendToAll(String message){
        for(InetAddress inetAddress: clientAddresses){
            String address = inetAddress.getHostAddress();
            send(message,address);
        }
    }

    public void sendToOwner(String message){
        String deviceAddress = mInfo.groupOwnerAddress.getHostAddress();
        send(message,deviceAddress);
    }

    public void send(String message, String address){
        Log.d(TAG, "send: ");
        Intent intent = new Intent(getBaseContext(),TransferMessageService.class);
        intent.setAction(TransferMessageService.ACTION_SEND_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putString(TransferMessageService.EXTRAS_MESSAGE,message);
        bundle.putString(TransferMessageService.EXTRAS_ADDRESS,address);
        bundle.putInt(TransferMessageService.EXTRAS_PORT,8888);
        intent.putExtras(bundle);
        startService(intent);
    }

    public void setNetworkToReadyState(boolean status, WifiP2pGroup group, WifiP2pDevice device, WifiP2pInfo info){
        p2pGroup = group;
        myDevice = device;
        mInfo = info;
        connectedAndReady = status;
        if(mInfo.isGroupOwner)id=0;
        if(justJoined){
            sendToOwner(FLAG_SEND_ADDRESS);
            justJoined = false;
        }
    }

    public void addID(int nID){
        id = nID;

        //start the async task to receive messages
        server = new ServerAsyncTask(this);
        server.execute();
    }

    public void addClient(InetAddress address){
        clientAddresses.add(address);

        //start the async task to receive messages
        server = new ServerAsyncTask(this);
        server.execute();

        send(FLAG_SEND_ID+clientAddresses.size(),address.getHostAddress());
    }

    // Leave it out because of complications with ID?
    public void removeClient(InetAddress address){
        for(int i =0; i<clientAddresses.size();i++){
            if(clientAddresses.get(i).equals(address)){
                clientAddresses.remove(i);
            }
        }

        //start the async task to receive messages
        server = new ServerAsyncTask(this);
        server.execute();
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
