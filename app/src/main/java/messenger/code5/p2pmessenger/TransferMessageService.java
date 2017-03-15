package messenger.code5.p2pmessenger;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TransferMessageService extends IntentService {
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_MESSAGE = "messenger.code5.p2pmessenger.SEND_MESSAGE";
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
            }catch (IOException e){

            }finally {
                if(socket!=null){
                    if(socket.isConnected()){
                        try{
                            socket.close();
                        }catch (IOException e){

                        }
                    }
                }
            }
        }
    }
}