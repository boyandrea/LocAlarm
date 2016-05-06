package kejar.sby.localarm.adapter;

import android.content.Context;
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

/**
 * Created by Irfan Septiadi Putra on 06/05/2016.
 */
public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.AlarmViewHolder> {


    private ArrayList<Alarm> listAlarm;
    private Context mContext;

    public AlarmRecyclerAdapter(ArrayList<Alarm> alarms,Context mContext){
        this.listAlarm = alarms;
        this.mContext = mContext;
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
        holder.switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.e("Switch",alarmItem.getId()+" is ON");
                }else {
                    Log.e("Switch",alarmItem.getId()+" is OFF");
                }
            }
        });
        if(alarmItem.getStatus()){
            holder.switchAlarm.setChecked(true);
        }
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
