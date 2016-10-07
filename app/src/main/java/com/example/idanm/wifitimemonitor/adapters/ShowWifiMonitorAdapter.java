package com.example.idanm.wifitimemonitor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.idanm.wifitimemonitor.R;
import com.example.idanm.wifitimemonitor.activities.ShowWifiMonitor;

import java.util.ArrayList;


/**
 * Created by idanm on 10/7/16.
 */
public class ShowWifiMonitorAdapter extends ArrayAdapter<ShowWifiMonitor.WifiStatusHistory> {
    public ArrayList<ShowWifiMonitor.WifiStatusHistory> wifiStatusHistory;

    public ShowWifiMonitorAdapter(Context context, int textViewResourceId, ArrayList<ShowWifiMonitor.WifiStatusHistory> wifiMonitorConnections) {
        super(context, textViewResourceId, wifiMonitorConnections);
        this.wifiStatusHistory = new ArrayList<ShowWifiMonitor.WifiStatusHistory>();
        this.wifiStatusHistory.addAll(wifiMonitorConnections);
    }

    private class ViewHolder {
        TextView ssidName;
        TextView connectionDate;
        TextView connectionTotalTime;
        TextView connectionFromTime;
        TextView connectionToTime;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.show_wifi_monitor, null);

            holder = new ViewHolder();
            holder.ssidName = (TextView) convertView.findViewById(R.id.showWifiMonitorSsidName);
            holder.connectionDate = (TextView) convertView.findViewById(R.id.showWifiMonitorConnectionDate);
            holder.connectionTotalTime = (TextView) convertView.findViewById(R.id.showWifiMonitorConnectionTotalTime);
            holder.connectionFromTime = (TextView) convertView.findViewById(R.id.showWifiMonitorConnectionFromTime);
            holder.connectionToTime = (TextView) convertView.findViewById(R.id.showWifiMonitorConnectionToTime);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        ShowWifiMonitor.WifiStatusHistory wifiStatusHistoryPosition = wifiStatusHistory.get(position);
        holder.ssidName.setText(wifiStatusHistoryPosition.getSsidName());
        holder.connectionDate.setText(wifiStatusHistoryPosition.getConnectionDate());
        holder.connectionTotalTime.setText(wifiStatusHistoryPosition.getConnectionTotalTime());
        holder.connectionFromTime.setText(wifiStatusHistoryPosition.getConnectionFromTime());
        holder.connectionToTime.setText(wifiStatusHistoryPosition.getConnectionToTime());
        return convertView;
    }

}
