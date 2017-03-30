package messenger.code5.p2pmessenger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Bastian Wieck on 3/15/2017.
 */

public class ServerAsyncTask extends AsyncTask<Void, Void, String>{
    private static final String TAG = "ServerAsyncTask";
    private Context context;
    private TextView statusText;

    public ServerAsyncTask(Context context, View statusText){
        this.context = context;
        this.statusText = (TextView)statusText;
    }

    @Override
    protected String doInBackground(Void... params){
        try{
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            InputStream inputStream = client.getInputStream();
            return inputStream.toString();
        }catch (IOException e){
            Log.e(TAG, "doInBackground: ",e );
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result){
        Log.d(TAG, "onPostExecute: ");
        if(result!=null){
            //result contains the incoming message
        }
    }
}
