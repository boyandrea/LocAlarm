package kejar.sby.localarm.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;

import java.util.ArrayList;

/**
 * Created by Irfan Septiadi Putra on 06/05/2016.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    double latitude, longitude;
    int radius;
    String dest;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    ArrayList<String> destination = new ArrayList<String>();
    AlarmDatabase alarmDatabase;
    MediaPlayer player;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmDatabase = new AlarmDatabase(context);
        destination = alarmDatabase.getListActiveAlarm();
        geoFire = new GeoFire(new Firebase(Constanta.FIREBASE_URL+"irfan"));
        if(player == null){
            player = MediaPlayer.create(context,Settings.System.DEFAULT_RINGTONE_URI);
        }
        Log.e("New Broadcast",intent.getAction());
        if(intent.getAction().equals(Constanta.ACTION_LOCATION)){
            latitude = intent.getDoubleExtra("latitude",-1);
            longitude = intent.getDoubleExtra("longitude",-1);
            sendToGeoFire(latitude,longitude);
        }else if(intent.getAction().equals(Constanta.ACTION_GEOFIRE_NEW)){
            dest = intent.getStringExtra("destination");
            latitude = intent.getDoubleExtra("latitude",-1);
            longitude = intent.getDoubleExtra("longitude",-1);
            radius = intent.getIntExtra("radius",0);
            Log.e("Set GeoFire",dest+" "+latitude+" "+longitude+" "+radius);
            setGeoQuery(latitude,longitude,radius,dest,context);
        }else if(intent.getAction().equals(Constanta.ACTION_GEOFIRE_REMOVE)){
            try{
                if(player.isPlaying()){
                    stopAlarm();
                    String stopDest = intent.getStringExtra("destination");
                    geoFire.removeLocation(stopDest);
                }else{
                    String stopDest = intent.getStringExtra("destination");
                    geoFire.removeLocation(stopDest);
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context,"Gagal mematikan alarm",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playAlarm(){
        player.start();
    }
    private void stopAlarm() {
        player.stop();
    }

    private void sendToGeoFire(double latitude, double longitude) {
        for (int i=0; i<destination.size();i++){
            getLocation(destination.get(i));
            geoFire.setLocation(destination.get(i), new GeoLocation(latitude,longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, FirebaseError error) {
                    if (error != null) {
                        System.err.println("There was an error saving the location to GeoFire: " + error);
                    } else {
                        //System.out.println("Location saved on server successfully!");
                    }
                }
            });
        }

    }

    private boolean getLocation(String key){
        geoFire.getLocation(key, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if(location != null){
                   Log.w("GetLocation",(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude)));
                }else{
                    Log.w("GetLocation",(String.format("There is no location for key %s in GeoFire", key)));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return true;
    }

    public void setGeoQuery(double lat,double lon, int radius,String destination, final Context ctx) {
        geoQuery = geoFire.queryAtLocation(new GeoLocation(lat,lon),radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation myLocation) {
                Log.w("GeoFire", (String.format("Key %s entered the search area at [%f,%f]", key, myLocation.latitude, myLocation.longitude)));
                Toast.makeText(ctx,"Sudah Sampai di "+key+"\n Matikan alarm dengan menggeser switch",Toast.LENGTH_SHORT).show();
                playAlarm();
            }

            @Override
            public void onKeyExited(String key) {
                Log.w("GeoFire", (String.format("Key %s is no longer in the search area", key)));
                //Toast.makeText(ctx,"Anda sudah berada diluar area "+key,Toast.LENGTH_LONG).show();
                stopAlarm();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.w("GeoFire", (String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude)));
            }

            @Override
            public void onGeoQueryReady() {
                Log.w("GeoFire", ("All initial data has been loaded and events have been fired!"));
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {
                Log.w("GeoFire", ("There was an error with this query: " + error));
            }
        });
    }

}