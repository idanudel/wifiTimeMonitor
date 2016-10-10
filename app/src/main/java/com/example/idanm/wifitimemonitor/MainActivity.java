package com.example.idanm.wifitimemonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.idanm.wifitimemonitor.activities.EditWifiMonitor;
import com.example.idanm.wifitimemonitor.activities.ShowWifiMonitor;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.OperationType;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorConnections;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorEntryEntity;
import com.example.idanm.wifitimemonitor.dataObjects.viewObjects.SsidName;
import com.example.idanm.wifitimemonitor.dataObjects.viewObjects.WifiMonitorEntry;
import com.example.idanm.wifitimemonitor.adapters.WifiMonitorEntryAdapter;
import com.example.idanm.wifitimemonitor.db.DbHelper;
import com.example.idanm.wifitimemonitor.receivers.WifiBroadcastReceiver;
import com.example.idanm.wifitimemonitor.receivers.WifiStatusUpdater;
import com.example.idanm.wifitimemonitor.utils.CONST;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiMonitorEntryAdapter wifiMonitorEntryAdapter = null;
    private ArrayList<WifiMonitorEntry> wifiMonitorEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        BroadcastReceiver broadcastReceiver = new WifiBroadcastReceiver();
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//        registerReceiver(broadcastReceiver, intentFilter);
        Toast.makeText(getApplicationContext(),
                "isWorking: "+new WifiStatusUpdater(getApplicationContext()).isAlarmUp(),
                Toast.LENGTH_LONG).show();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditWifiMonitor.class);
                startActivityForResult(intent,3);
            }
        });

        generateWifiMonitorListView();
        this.registerReceiver(mMessageReceiver, new IntentFilter(CONST.UPDATE_UI_ACTION));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            generateWifiMonitorListView();
        }
    };

    private void generateWifiMonitorListView() {
        ArrayList<WifiMonitorEntryEntity> wifiMonitorEntryEntity = getWifiMonitorEntryEntity();
        ArrayList<WifiMonitorEntry> wifiMonitorEntry = getWifiMonitorEntries(wifiMonitorEntryEntity);
        wifiMonitorEntryAdapter = new WifiMonitorEntryAdapter(this,
                R.layout.content_edit_wifi_monitor, wifiMonitorEntry);
        ListView listView = (ListView) findViewById(R.id.wifiMonitorEntries);
        // Assign adapter to ListView
        listView.setAdapter(wifiMonitorEntryAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                WifiMonitorEntry wifiMonitorEntry = (WifiMonitorEntry) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + wifiMonitorEntry.getEntryName() + " "+wifiMonitorEntry.getWifiMonitorEntryId(),
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(view.getContext(), ShowWifiMonitor.class);
                int wifiKey = wifiMonitorEntry.getWifiMonitorEntryId();
                intent.putExtra("wifiKey", wifiKey);
                startActivity(intent);
            }
        });
    }

    private ArrayList<WifiMonitorEntry> getWifiMonitorEntries(ArrayList<WifiMonitorEntryEntity> wifiMonitorEntryEntities) {
        ArrayList<WifiMonitorEntry> wifiMonitorEntries = new ArrayList<>();
        if(wifiMonitorEntryEntities == null || wifiMonitorEntryEntities.size()<1){
            return wifiMonitorEntries;
        }
        for (WifiMonitorEntryEntity wifiMonitorEntryEntity:wifiMonitorEntryEntities){
            WifiMonitorEntry wifiMonitorEntry = new WifiMonitorEntry();
            wifiMonitorEntry.setWifiMonitorEntryId(wifiMonitorEntryEntity.getId());
            wifiMonitorEntry.setEntryName(wifiMonitorEntryEntity.getWifiMonitorEntryName());
            long lastConnectionDay = getLastConnectedDay(wifiMonitorEntryEntity.getWifiMonitorConnectionses());

            if(lastConnectionDay!=0) {
                long firstConnectionOfDay = firstConnectionDay(wifiMonitorEntryEntity.getWifiMonitorConnectionses(),lastConnectionDay);
                wifiMonitorEntry.setConnectionDate(lastConnectionDay);
                if(firstConnectionOfDay==0){
                    firstConnectionOfDay = lastConnectionDay;
                }
                wifiMonitorEntry.setConnectedTime(lastConnectionDay - firstConnectionOfDay);
            }
            wifiMonitorEntries.add(wifiMonitorEntry);
        }
        return wifiMonitorEntries;

    }

    private long firstConnectionDay(ArrayList<WifiMonitorConnections> wifiMonitorConnectionses, long lastConnectionDayTime) {
        long firstConnectionDay = lastConnectionDayTime;
        if(wifiMonitorConnectionses==null || wifiMonitorConnectionses.size()<1){
            return firstConnectionDay;
        }
        GregorianCalendar lastConnectionDay = new GregorianCalendar();
        lastConnectionDay.setTimeInMillis(lastConnectionDayTime);
        for(WifiMonitorConnections wifiMonitorConnections:wifiMonitorConnectionses){
            long connectionDate = wifiMonitorConnections.getDate().getTime();
            GregorianCalendar currentCal = new GregorianCalendar();
            currentCal.setTimeInMillis(connectionDate);
            int currentDayofYear = currentCal.get(Calendar.DAY_OF_YEAR);
            int dayToComapre = lastConnectionDay.get(Calendar.DAY_OF_YEAR);
            if(currentDayofYear==dayToComapre && connectionDate<firstConnectionDay){
                firstConnectionDay = connectionDate;
            }
        }
        return firstConnectionDay;
    }

    private long getLastConnectedDay(ArrayList<WifiMonitorConnections> wifiMonitorConnectionses) {
        if(wifiMonitorConnectionses==null || wifiMonitorConnectionses.size()<1){
            return 0;
        }
        GregorianCalendar lastConnectionDay = new GregorianCalendar();
        lastConnectionDay.setTimeInMillis(0);
        for(WifiMonitorConnections wifiMonitorConnections:wifiMonitorConnectionses){
            long connectionDate = wifiMonitorConnections.getDate().getTime();
            GregorianCalendar currentCal = new GregorianCalendar();
            currentCal.setTimeInMillis(connectionDate);
            int lastConnectionDayOfYear = lastConnectionDay.get(Calendar.DAY_OF_YEAR);
            int currentDayofYear = currentCal.get(Calendar.DAY_OF_YEAR);
            if(currentDayofYear>lastConnectionDayOfYear && OperationType.CONNECT.name().equals(wifiMonitorConnections.getOp())){
                lastConnectionDay.setTimeInMillis(connectionDate);
            }
        }
        return lastConnectionDay.getTimeInMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh) {
            generateWifiMonitorListView();
        }
        if (id == R.id.action_clear_all) {
            clearAllEntryies();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearAllEntryies() {
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.clearAll();
        generateWifiMonitorListView();
    }

    public ArrayList<WifiMonitorEntryEntity> getWifiMonitorEntryEntity() {
        DbHelper dbHelper = new DbHelper(this);
        return dbHelper.getWifiAllMonitorEntries();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode) {
            case (Activity.RESULT_OK) : {
                    generateWifiMonitorListView();
             break;
            }
        }
    }
}
