package messenger.code5.p2pmessenger;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.util.Log;

import com.sababado.circularview.Marker;
import com.sababado.circularview.SimpleCircularViewAdapter;

/**
 * Created by Bastian Wieck on 3/14/2017.
 */

public class CircularViewAdapter extends SimpleCircularViewAdapter {
    private int size;

    public CircularViewAdapter(int size){
        super();
        this.size = size;
        Log.d("TEST", "CircularViewAdapter: ");
    }


    @Override
    public int getCount(){
        Log.d("TEST", "getCount: "+size);
        return size;
    }

    @Override
    public void setupMarker(final int position, final Marker marker){
        Log.d("TEST", "setupMarker: ");
        marker.setSrc(R.mipmap.green_swirl);
        marker.setFitToCircle(true);
        if(size<=30)marker.setRadius(80);
        else if(size<65) marker.setRadius(140-2*size);
        else marker.setRadius(10);
    }
}