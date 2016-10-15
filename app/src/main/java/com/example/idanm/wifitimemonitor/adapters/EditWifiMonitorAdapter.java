package com.example.idanm.wifitimemonitor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.idanm.wifitimemonitor.dataObjects.viewObjects.SsidName;
import com.example.idanm.wifitimemonitor.R;

import java.util.ArrayList;

/**
 * Created by idanm on 9/29/16.
 */

public class EditWifiMonitorAdapter extends ArrayAdapter<SsidName> {

    public ArrayList<SsidName> ssidList;

    public EditWifiMonitorAdapter(Context context, int textViewResourceId,
                                  ArrayList<SsidName> StringList) {
        super(context, textViewResourceId, StringList);
        this.ssidList = new ArrayList<SsidName>();
        this.ssidList.addAll(StringList);
    }

    private class ViewHolder {
        TextView ssidName;
        CheckBox name;
    }
    @Override
    public int getCount() {
        return ssidList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.ssid_select_menu, null);

            holder = new ViewHolder();
            holder.ssidName = (TextView) convertView.findViewById(R.id.code);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    SsidName ssidName = (SsidName) cb.getTag();
//                    Toast.makeText(getContext().getApplicationContext(),
//                            "Clicked on Checkbox: " + cb.getText() +
//                                    " is " + cb.isChecked(),
//                            Toast.LENGTH_LONG).show();
                    ssidName.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        SsidName ssidName = ssidList.get(position);
        holder.ssidName.setText(ssidName.getSsidName());
        holder.name.setChecked(ssidName.isSelected());
        holder.name.setTag(ssidName);
        return convertView;

    }




}
