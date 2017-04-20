package messenger.code5.p2pmessenger;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TransferMessageService extends IntentService {
    private static final String TAG = "TransferMessageService";

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_MESSAGE = "messenger.code5.p2pmessenger.FLAG_SEND_MESSAGE";
    public static final String EXTRAS_MESSAGE = "message";
    public static final String EXTRAS_ADDRESS = "go_host";
    public static final String EXTRAS_PORT = "go_port";

    public TransferMessageService(String name){
        super(name);
    }

    public TransferMessageService(){
        super("TransferMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.d(TAG, "onHandleIntent: ");
        Context context = getApplicationContext();
        if(intent.getAction().equals(ACTION_SEND_MESSAGE)){
            String message = intent.getExtras().getString(EXTRAS_MESSAGE);
            String host = intent.getExtras().getString(EXTRAS_ADDRESS);
            int port = intent.getExtras().getInt(EXTRAS_PORT);
            Socket socket = new Socket();
            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(host,port)),SOCKET_TIMEOUT);
                OutputStream outputStream = socket.getOutputStream();
                byte[] bytes = message.getBytes();
                outputStream.write(bytes);
                Log.d(TAG, "onHandleIntent: ");
            }catch (IOException e){
                e.printStackTrace();
                Log.e(TAG, "IOException onHandleIntent: ", e);
            }finally {
                if(socket!=null){
                    if(socket.isConnected()){
                        try{
                            socket.close();
                        }catch (IOException e){
                            Log.e(TAG, "IOException while closing onHandleIntent: ", e);
                        }
                    }
                }
            }
        }
    }
}
