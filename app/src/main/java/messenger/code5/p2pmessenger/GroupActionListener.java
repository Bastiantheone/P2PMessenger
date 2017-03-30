package messenger.code5.p2pmessenger;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Bastian Wieck on 3/29/2017.
 */

public class GroupActionListener implements WifiP2pManager.ActionListener{
    @Override
    public void onSuccess() {
        Log.d("Test", "createGroup onSuccess: ");
    }

    @Override
    public void onFailure(int i) {
        Log.d("Test", "createGroup onFailure: ");
    }
}
