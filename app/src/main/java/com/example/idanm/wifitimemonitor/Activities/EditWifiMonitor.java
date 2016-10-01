package com.example.idanm.wifitimemonitor.Activities;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idanm.wifitimemonitor.DataObjects.EditWifiMonitorAdapter;
import com.example.idanm.wifitimemonitor.DataObjects.SsidName;
import com.example.idanm.wifitimemonitor.R;
import com.example.idanm.wifitimemonitor.db.DbHelper;

import java.util.ArrayList;

public class EditWifiMonitor extends AppCompatActivity {
    EditWifiMonitorAdapter editWifiMonitorAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wifi_monitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WifiManager wifiManager =
                (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.getConfiguredNetworks()==null){
            //todo print current message on screen
            return;
        }
        ArrayList<SsidName> ssidList = new ArrayList<SsidName>();
        for(WifiConfiguration wifiConfiguration:wifiManager.getConfiguredNetworks()){
            SsidName ssidName = new SsidName(wifiConfiguration.SSID,false);
            ssidList.add(ssidName);

        }
        //create an ArrayAdaptar from the String Array
        editWifiMonitorAdapter = new EditWifiMonitorAdapter(this,
                R.layout.content_edit_wifi_monitor, ssidList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(editWifiMonitorAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                SsidName ssidName = (SsidName) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + ssidName.getSsidName(),
                        Toast.LENGTH_LONG).show();
            }
        });
        checkButtonClick();

    }
    private void checkButtonClick() {
        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView selectedWifiTrackerName = (TextView) findViewById(R.id.selectWifiTrackerName);
                if(selectedWifiTrackerName.getText()==null || selectedWifiTrackerName.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext().getApplicationContext(),
                            "Select Name First", Toast.LENGTH_SHORT).show();
                }

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<SsidName> StringList = editWifiMonitorAdapter.ssidList;
                ArrayList<String> ssidNames = new ArrayList<String>();
                for(int i=0;i<StringList.size();i++){
                    SsidName ssidName = StringList.get(i);
                    if(ssidName.isSelected()){
                        responseText.append("\n" + ssidName.getSsidName());
                        ssidNames.add(ssidName.getSsidName());
                    }
                }

                Toast.makeText(getApplicationContext().getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
                DbHelper dbHelper = new DbHelper(getApplicationContext());
                String wifiMonitorEntryName = selectedWifiTrackerName.getText().toString();
                dbHelper.insertWifiTimeMonitorSetting(wifiMonitorEntryName,ssidNames);

            }
        });

    }

}