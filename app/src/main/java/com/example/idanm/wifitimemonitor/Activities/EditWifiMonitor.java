package com.example.idanm.wifitimemonitor.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idanm.wifitimemonitor.MainActivity;
import com.example.idanm.wifitimemonitor.adapters.EditWifiMonitorAdapter;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.AssociateSsids;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorEntryEntity;
import com.example.idanm.wifitimemonitor.dataObjects.viewObjects.SsidName;
import com.example.idanm.wifitimemonitor.R;
import com.example.idanm.wifitimemonitor.db.DbHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditWifiMonitor extends AppCompatActivity {
    EditWifiMonitorAdapter editWifiMonitorAdapter = null;
    Integer wifiMonitorEntreyKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wifi_monitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addManualSsid);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditWifiMonitor.this);
                alertDialogBuilder.setTitle("Add SSID name");

                final EditText et = new EditText(EditWifiMonitor.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Editable ssidNameText = et.getText();
                        if(ssidNameText!=null && !ssidNameText.toString().isEmpty()){
                            SsidName ssidName = new SsidName(ssidNameText.toString(),true);
                            editWifiMonitorAdapter.ssidList.add(0,ssidName);
                            editWifiMonitorAdapter.notifyDataSetChanged();
                        }
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();


            }});


        final WifiManager wifiManager =
                (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.getConfiguredNetworks()==null){
            //todo print current message on screen
            return;
        }
        final Set<SsidName> ssidList = new HashSet<>();

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null && bd.get("wifiKey")!=null && (bd.get("wifiKey") instanceof Integer)){
            wifiMonitorEntreyKey = (Integer) bd.get("wifiKey");
            DbHelper dbHelper = new DbHelper(getApplicationContext());
            WifiMonitorEntryEntity wifiMonitorEntryEntity = dbHelper.getWifiMonitorEntries(wifiMonitorEntreyKey);
            if(wifiMonitorEntryEntity!=null){
                TextView selectedWifiTrackerName = (TextView) findViewById(R.id.selectWifiTrackerName);
                selectedWifiTrackerName.setText(wifiMonitorEntryEntity.getWifiMonitorEntryName());
                ArrayList<AssociateSsids>  associateSsids = wifiMonitorEntryEntity.getAssociateSsidses();
                if(associateSsids!=null && associateSsids.size()>0){
                    for(AssociateSsids associateSsid:associateSsids){
                        SsidName ssidName = new SsidName(associateSsid.getSsidName(),true);
                        ssidList.add(ssidName);
                    }
                }

            }
        }

        for(WifiConfiguration wifiConfiguration:wifiManager.getConfiguredNetworks()){
            SsidName ssidName = new SsidName(wifiConfiguration.SSID,false);
            ssidList.add(ssidName);
        }


        //create an ArrayAdaptar from the String Array
        editWifiMonitorAdapter = new EditWifiMonitorAdapter(this,
                R.layout.content_edit_wifi_monitor, new ArrayList<SsidName>(ssidList));
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(editWifiMonitorAdapter);

        //add scan ssid names
        if (wifiManager.isWifiEnabled() == true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }
            //wifiManager.setWifiEnabled(true);
            registerReceiver(new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context c, Intent intent)
                {
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    if(scanResults==null || scanResults.size()<1)return;
                    for(ScanResult scanResult:scanResults){
                        SsidName ssidNameScan = new SsidName(scanResult.SSID,false);
                        if(!editWifiMonitorAdapter.ssidList.contains(ssidNameScan)) {
                            editWifiMonitorAdapter.ssidList.add(ssidNameScan);
                        }
                    }
                    editWifiMonitorAdapter.notifyDataSetChanged();


                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        }
        wifiManager.startScan();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                SsidName ssidName = (SsidName) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + ssidName.getSsidName(),
                        Toast.LENGTH_LONG).show();
            }
        });
        checkButtonClick();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

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
                    return;
                }


                ArrayList<SsidName> availableSsidNames = editWifiMonitorAdapter.ssidList;
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");


                ArrayList<String> ssidNames = new ArrayList<String>();
                for(int i=0;i<availableSsidNames.size();i++){
                    SsidName ssidName = availableSsidNames.get(i);
                    if(ssidName.isSelected()){
                        responseText.append("\n" + ssidName.getSsidName());
                        ssidNames.add(ssidName.getSsidName());
                    }
                }

                if(ssidNames==null || ssidNames.isEmpty()){
                    Toast.makeText(getApplicationContext().getApplicationContext(),
                            "Select at least one SSID name", Toast.LENGTH_SHORT).show();
                    return;
                }

                DbHelper dbHelper = new DbHelper(getApplicationContext());
                String wifiMonitorEntryName = selectedWifiTrackerName.getText().toString();
                Intent intent;
                if(wifiMonitorEntreyKey==null) {
                    dbHelper.insertWifiTimeMonitorSetting(wifiMonitorEntryName, ssidNames);
                     intent = new Intent(getApplicationContext(), MainActivity.class);
                }else {
                    dbHelper.updateWifiTimeMonitorSetting(wifiMonitorEntryName, wifiMonitorEntreyKey, ssidNames);
                     intent = new Intent(getApplicationContext(), ShowWifiMonitor.class);
                }

                // TODO Add extras or a data URI to this intent as appropriate.
                setResult(Activity.RESULT_OK, intent);
                finish();


            }
        });

    }



}
