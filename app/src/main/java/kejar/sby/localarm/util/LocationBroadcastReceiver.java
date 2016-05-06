package kejar.sby.localarm.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Irfan Septiadi Putra on 06/05/2016.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    double latitude, longitude;
    String username;
    int id_alarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast","Lokasi Baru");

    }
}
