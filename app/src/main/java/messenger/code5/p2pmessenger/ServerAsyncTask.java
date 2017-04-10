package messenger.code5.p2pmessenger;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.RecyclerView;
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
    private MainActivity mainActivity;

    public ServerAsyncTask(MainActivity mainActivity){
        this.mainActivity = mainActivity;
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
            mainActivity.addMessage(result);

            //create a notification
           /* NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("P2P")
                    .setContentText(result);

            Intent resultIntent = new Intent(context,MessageActivity);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MessageActivity);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);*/
        }
    }
}
