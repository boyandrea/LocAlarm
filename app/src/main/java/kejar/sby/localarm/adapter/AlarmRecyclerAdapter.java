package kejar.sby.localarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import kejar.sby.localarm.R;
import kejar.sby.localarm.model.Alarm;
import kejar.sby.localarm.util.AlarmDatabase;
import kejar.sby.localarm.util.Constanta;
import kejar.sby.localarm.util.LocationBroadcastReceiver;

/**
 * Created by Irfan Septiadi Putra on 06/05/2016.
 */
public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.AlarmViewHolder> {


    private ArrayList<Alarm> listAlarm;
    private Context mContext;
    private AlarmDatabase alarmDatabase;
    LocationBroadcastReceiver bcReceiver;

    public AlarmRecyclerAdapter(Context context,ArrayList<Alarm> list){
        this.mContext = context;
        alarmDatabase = new AlarmDatabase(mContext);
        this.listAlarm = list;
        bcReceiver = new LocationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(Constanta.ACTION_GEOFIRE_NEW);
        intentFilter.addAction(Constanta.ACTION_GEOFIRE_REMOVE);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(bcReceiver,intentFilter);
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_alarm,parent,false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        final Alarm alarmItem = listAlarm.get(position);
        holder.txtDestination.setText(alarmItem.getDestination());
        holder.txtRadius.setText("Radius "+alarmItem.getRadius()+" Km");
        Log.e("Status",""+alarmItem.getStatus());
        if(alarmItem.getStatus() == 1){
            holder.switchAlarm.setChecked(true);
            setGeoQuery(alarmItem);
        }else{
            holder.switchAlarm.setChecked(false);
        }

        holder.switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.e("Switch",alarmItem.getId()+" is ON");
                    alarmDatabase.setStatus(alarmItem.getId(),1);
                    setGeoQuery(alarmItem);
                }else {
                    Log.e("Switch",alarmItem.getId()+" is OFF");
                    alarmDatabase.setStatus(alarmItem.getId(),0);
                    setAlarmOff(alarmItem);
                }
            }
        });
    }

    public void setGeoQuery(Alarm myAlarm){
        Intent intent = new Intent();
        intent.setAction(Constanta.ACTION_GEOFIRE_NEW);
        intent.putExtra("id",myAlarm.getId());
        intent.putExtra("destination",myAlarm.getDestination());
        intent.putExtra("latitude",myAlarm.getLatitude());
        intent.putExtra("longitude",myAlarm.getLongitude());
        intent.putExtra("radius",myAlarm.getRadius());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void setAlarmOff(Alarm myAlarm){
        Intent intent = new Intent();
        intent.setAction(Constanta.ACTION_GEOFIRE_REMOVE);
        intent.putExtra("id",myAlarm.getId());
        intent.putExtra("destination",myAlarm.getDestination());
        intent.putExtra("latitude",myAlarm.getLatitude());
        intent.putExtra("longitude",myAlarm.getLongitude());
        intent.putExtra("radius",myAlarm.getRadius());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }


    @Override
    public int getItemCount() {

        return listAlarm.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDestination;
        public TextView txtRadius;
        public SwitchCompat switchAlarm;

        public AlarmViewHolder (View itemView){
            super(itemView);
            txtDestination = (TextView) itemView.findViewById(R.id.txtDestination);
            txtRadius = (TextView) itemView.findViewById(R.id.txtRadius);
            switchAlarm = (SwitchCompat) itemView.findViewById(R.id.switchAlarm);

        }
    }
}
