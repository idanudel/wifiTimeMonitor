package com.example.idanm.wifitimemonitor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.idanm.wifitimemonitor.dataObjects.viewObjects.WifiMonitorEntry;
import com.example.idanm.wifitimemonitor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by idanm on 10/1/16.
 */
public class WifiMonitorEntryAdapter extends ArrayAdapter<WifiMonitorEntry> {

    public ArrayList<WifiMonitorEntry> wifiMonitorEntries;

    public WifiMonitorEntryAdapter(Context context, int textViewResourceId,
                                  ArrayList<WifiMonitorEntry> wifiMonitorEntry) {
        super(context, textViewResourceId, wifiMonitorEntry);
        this.wifiMonitorEntries = new ArrayList<WifiMonitorEntry>();
        this.wifiMonitorEntries.addAll(wifiMonitorEntry);
    }

    private class ViewHolder {
        TextView entryName;
        TextView entryDate;
        TextView entryTime;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.wif_monitor_entry, null);

            holder = new ViewHolder();
            holder.entryName = (TextView) convertView.findViewById(R.id.entryName);
            holder.entryDate = (TextView) convertView.findViewById(R.id.entryDate);
            holder.entryTime = (TextView) convertView.findViewById(R.id.entryTime);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        WifiMonitorEntry wifiMonitorEntry = wifiMonitorEntries.get(position);
        holder.entryName.setText(wifiMonitorEntry.getEntryName());
        holder.entryName.setTag(wifiMonitorEntry.getWifiMonitorEntryId());

        if(wifiMonitorEntry.getConnectionDate()!=0){
            String formatString = "dd.MM.yy";
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            String date = format.format(new Date(wifiMonitorEntry.getConnectionDate()));
            holder.entryDate.setText(date);
        }
        else{
            holder.entryDate.setText("--.--.--");
        }


        long hours = wifiMonitorEntry.getConnectedTime() / (60 * 60 * 1000); //since both are ints, you get an int
        long minutes = wifiMonitorEntry.getConnectedTime() / (60 * 1000) % 60;
        if(hours!=0 || minutes!=0) {
            holder.entryTime.setText(hours + ":" + minutes);
        }
        else{
            holder.entryTime.setText("--:--");
        }

        return convertView;

    }




}
