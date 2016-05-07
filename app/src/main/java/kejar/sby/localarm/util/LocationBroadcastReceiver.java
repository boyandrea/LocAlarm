package kejar.sby.localarm.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

/**
 * Created by Irfan Septiadi Putra on 06/05/2016.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    double latitude, longitude;
    int radius;
    int id_alarm;
    private GeoFire geoFire;
    private GeoQuery geoQuery;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("Broadcast","New broadcast "+intent.getAction());
        if(intent.getAction().equals(Constanta.ACTION_LOCATION)){
            latitude = intent.getDoubleExtra("latitude",-1);
            longitude = intent.getDoubleExtra("longitude",-1);
            sendToGeoFire(latitude,longitude);
        }else if(intent.getAction().equals(Constanta.ACTION_GEOFIRE_NEW)){
            id_alarm = intent.getIntExtra("id",1);
            latitude = intent.getDoubleExtra("latitude",-1);
            longitude = intent.getDoubleExtra("longitude",-1);
            radius = intent.getIntExtra("radius",0);
            setGeoQuery(latitude,longitude,radius,id_alarm,context);
        }else if(intent.getAction().equals(Constanta.ACTION_GEOFIRE_ENTERED)){
            Toast.makeText(context,"Sudah Sampai",Toast.LENGTH_LONG).show();
        }

    }

    private void sendToGeoFire(double latitude, double longitude) {
        geoFire = new GeoFire(new Firebase(Constanta.FIREBASE_URL));
        geoFire.setLocation("set-location/irfan/1", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, FirebaseError error) {
                if(error != null){
                    Log.e("Send Location","Gagal...");
                }else{
                    Log.e("Send Location","Sukses...");

                }
            }
        });
    }

    public void setGeoQuery(double lat,double lon, int radius, int id, final Context ctx) {
        geoFire = new GeoFire(new Firebase(Constanta.FIREBASE_URL+"/set-location/irfan/"+id));
        GeoQuery  geoQuery = geoFire.queryAtLocation(new GeoLocation(lat,lon),radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation myLocation) {
                Log.w("GeoFire", (String.format("Key %s entered the search area at [%f,%f]", key, myLocation.latitude, myLocation.longitude)));
                Toast.makeText(ctx,"Sudah Sampai",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onKeyExited(String key) {
                Log.w("GeoFire", (String.format("Key %s is no longer in the search area", key)));
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