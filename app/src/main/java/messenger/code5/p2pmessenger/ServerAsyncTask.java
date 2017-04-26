package messenger.code5.p2pmessenger;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Bastian Wieck on 3/15/2017.
 */

public class ServerAsyncTask extends AsyncTask<Void, Void, String>{
    private static final String TAG = "ServerAsyncTask";
    private MainActivity mainActivity;
    private ServerSocket serverSocket;
    private InetAddress address;

    public ServerAsyncTask(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Void... params){
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(8888));
            Socket client = serverSocket.accept();

            InputStream inputStream = client.getInputStream();
            address = client.getInetAddress();
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        }catch (IOException e){
            Log.e(TAG, "doInBackground: ",e );
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result){
        Log.d(TAG, "onPostExecute: "+result);
        try{
            serverSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        if(result!=null){
            if(result.equals(MainActivity.FLAG_SEND_ADDRESS)){
                // new client sends an empty message to retrieve the address
                mainActivity.addClient(address);
            }
            else if(result.startsWith(MainActivity.FLAG_SEND_MESSAGE)) {
                // set start and end flags, because ID could be double digit
                int start = result.indexOf(MainActivity.FLAG_SEND_MESSAGE,1);
                int id = Integer.parseInt(result.substring(1,start));
                //result contains the incoming message
                String message = result.substring(start+1);
                mainActivity.receiveMessage(message,id);

                // FIXME create a notification something like this
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
            else if(result.startsWith(MainActivity.FLAG_SEND_ID)){
                int id = Integer.parseInt(result.substring(1));
                mainActivity.addID(id);
            }
        }
    }
}
