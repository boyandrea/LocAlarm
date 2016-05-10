package kejar.sby.localarm.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import kejar.sby.localarm.R;
import kejar.sby.localarm.adapter.AlarmRecyclerAdapter;
import kejar.sby.localarm.model.Alarm;
import kejar.sby.localarm.util.AlarmDatabase;
import kejar.sby.localarm.util.Constanta;
import kejar.sby.localarm.util.LocAlarmApp;
import kejar.sby.localarm.util.LocAlarmService;
import kejar.sby.localarm.util.LocationBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private RecyclerView alarmRecycler;
    private FloatingActionButton btnAddAlarm;
    private GoogleApiClient mGoogleApiClient;
    private int radiusValue = 0;
    private AlarmDatabase alarmDB;
    private AlarmRecyclerAdapter alarmAdapter;
    LocAlarmService alarmService;
    LocationBroadcastReceiver lbsReceiver;
    ArrayList<Alarm> alarms = new ArrayList<Alarm>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmDB = new AlarmDatabase(this);
        alarmRecycler = (RecyclerView) findViewById(R.id.list_alarm);
        btnAddAlarm = (FloatingActionButton) findViewById(R.id.btnAddAlarm);
        btnAddAlarm.setOnClickListener(this);
        setupToolbar();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        alarmService = new LocAlarmService(this);
        lbsReceiver = new LocationBroadcastReceiver();
        registerReceiver(lbsReceiver,new IntentFilter(Constanta.ACTION_LOCATION));
        registerReceiver(lbsReceiver,new IntentFilter(Constanta.ACTION_GEOFIRE_NEW));
        registerReceiver(lbsReceiver,new IntentFilter(Constanta.ACTION_GEOFIRE_REMOVE));

        if(alarms.size() != 0){
            alarms.clear();
        }
        alarms = alarmDB.getListAlarm();
        setupListAlarm();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(lbsReceiver);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void setupListAlarm(){
        if(alarms.size() != 0){
            alarms.clear();
        }
        alarms = alarmDB.getListAlarm();
        alarmAdapter = new AlarmRecyclerAdapter(this,alarms);
        alarmRecycler.setHasFixedSize(true);
        alarmRecycler.setAdapter(alarmAdapter);
        alarmRecycler.setLayoutManager(new LinearLayoutManager(this));
        alarmRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    public Alarm searchPlace(){
        final Alarm newAlarm = new Alarm();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
               newAlarm.setDestination(place.getName().toString());
               newAlarm.setStatus(1);
               newAlarm.setLatitude(place.getLatLng().latitude);
               newAlarm.setLongitude(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e("Place", "An error occurred: " + status);
            }
        });
        return newAlarm;
    }

    @Override
    public void onClick(View v) {
            showNewAlarmDialog();
    }

    public void showNewAlarmDialog(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.dialogTitle);
        alertDialog.setView(R.layout.new_alarm_dialog);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.new_alarm_dialog,null);
        alertDialog.setView(dialogView);
        final SeekBar radius = (SeekBar) dialogView.findViewById(R.id.radius);
        final TextView txtRadius = (TextView) dialogView.findViewById(R.id.txtRadius);
        final Alarm myAlarm = searchPlace();
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtRadius.setText("Alert Before : "+progress+" Km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusValue = seekBar.getProgress();
            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myAlarm.setRadius(radiusValue);
                alarmDB.setAlarm(myAlarm);
                Intent intent = new Intent();
                intent.setAction(Constanta.ACTION_GEOFIRE_NEW);
                intent.putExtra("id",myAlarm.getId());
                intent.putExtra("destination",myAlarm.getDestination());
                intent.putExtra("latitude",myAlarm.getLatitude());
                intent.putExtra("longitude",myAlarm.getLongitude());
                intent.putExtra("radius",myAlarm.getRadius());
                sendBroadcast(intent);
                setupListAlarm();

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
