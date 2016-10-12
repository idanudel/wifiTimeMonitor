package com.example.idanm.wifitimemonitor.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idanm.wifitimemonitor.R;
import com.example.idanm.wifitimemonitor.adapters.EditWifiMonitorAdapter;
import com.example.idanm.wifitimemonitor.adapters.ShowWifiMonitorAdapter;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorConnections;
import com.example.idanm.wifitimemonitor.db.DbEntriesParams;
import com.example.idanm.wifitimemonitor.db.DbHelper;
import com.example.idanm.wifitimemonitor.utils.CONST;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ShowWifiMonitor extends AppCompatActivity {
    ShowWifiMonitorAdapter showWifiMonitorAdapter =null;
    Integer wifiKey =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wifi_monitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd == null || bd.get("wifiKey")==null || !(bd.get("wifiKey") instanceof Integer)){
            Log.e(CONST.TAG,"No wifiKey found for ShowWifiMonitor");
            Toast.makeText(getApplicationContext(),
                    "Error occurred please try again later.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        wifiKey = (Integer)bd.get("wifiKey");
        generateWifiMonitorListView();
        this.registerReceiver(mMessageReceiver, new IntentFilter(CONST.UPDATE_UI_ACTION));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_show_wifi_monitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh_wifiRefresh) {
            generateWifiMonitorListView();
        }


        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            generateWifiMonitorListView();
        }
    };
    private void generateWifiMonitorListView(){
        ArrayList<WifiMonitorConnections> wifiMonitorConnections= new DbHelper(this).getWifiMonitorConnectionses(wifiKey.intValue());

        ArrayList<WifiStatusHistory> wifiStatusHistorys = organizeWifiMonitorConnections(wifiMonitorConnections);
        Collections.sort(wifiStatusHistorys);
        showWifiMonitorAdapter = new ShowWifiMonitorAdapter(this,R.layout.content_edit_wifi_monitor, wifiStatusHistorys);
        ListView listView = (ListView) findViewById(R.id.showWifiMonitorListView);
        listView.setAdapter(showWifiMonitorAdapter);
    }

    private ArrayList<WifiStatusHistory> organizeWifiMonitorConnections(ArrayList<WifiMonitorConnections> wifiMonitorConnections) {
        ArrayList<WifiStatusHistory> wifiStatusHistories = new ArrayList<>();
        if(wifiMonitorConnections==null || wifiMonitorConnections.isEmpty()){
            return wifiStatusHistories;
        }
        HashMap<Long,WifiStatusHistory> stringWifiStatusHistoryHashMap = new HashMap<>();
        for(WifiMonitorConnections wifiMonitorConnection:wifiMonitorConnections){
            long startOfDay = new DbHelper(getApplicationContext()).getStartOfDay(wifiMonitorConnection.getDate());
            if(!stringWifiStatusHistoryHashMap.containsKey(startOfDay)){
                WifiStatusHistory wifiStatusHistory = new WifiStatusHistory();
                String formatString = "dd/MM/yy";
                SimpleDateFormat format = new SimpleDateFormat(formatString);
                wifiStatusHistory.setConnectionDate(format.format(wifiMonitorConnection.getDate()));
                wifiStatusHistory.setConnectionFromTime(getHourAndMin(wifiMonitorConnection.getDate().getTime()));
                wifiStatusHistory.setConnectionFromTimeLong(wifiMonitorConnection.getDate().getTime());
                wifiStatusHistory.setSsidName(wifiMonitorConnection.getSsidName());
                stringWifiStatusHistoryHashMap.put(startOfDay,wifiStatusHistory);
                continue;
            }

            WifiStatusHistory wifiStatusHistory = stringWifiStatusHistoryHashMap.get(startOfDay);
            Long currentConnectionTime = wifiMonitorConnection.getDate().getTime();
            Long fromConnectionTime = wifiStatusHistory.getConnectionFromTimeLong();
            Long toConnectionTime = wifiStatusHistory.getConnectionToTimeLong();

            if(fromConnectionTime>-1 && currentConnectionTime<fromConnectionTime){
                Long temp = fromConnectionTime;
                fromConnectionTime = currentConnectionTime;
                currentConnectionTime = temp;
                wifiStatusHistory.setConnectionFromTime(getHourAndMin(fromConnectionTime));
                wifiStatusHistory.setConnectionFromTimeLong(fromConnectionTime);

            }
            if((toConnectionTime==null && currentConnectionTime !=null) || currentConnectionTime>toConnectionTime){
                toConnectionTime = currentConnectionTime;
                wifiStatusHistory.setConnectionToTime(getHourAndMin(toConnectionTime));
                wifiStatusHistory.setConnectionToTimeLong(toConnectionTime);
            }

            if(toConnectionTime!=-1 && fromConnectionTime!=-1){
                long totalTime = wifiStatusHistory.getConnectionToTimeLong() - wifiStatusHistory.getConnectionFromTimeLong();
                wifiStatusHistory.setConnectionTotalTime(getTimeStringFormat(totalTime));
            }
            stringWifiStatusHistoryHashMap.put(startOfDay,wifiStatusHistory);
        }

        wifiStatusHistories = new ArrayList<>(stringWifiStatusHistoryHashMap.values());
        return wifiStatusHistories;
    }

    private String getHourAndMin(Long time) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTimeInMillis(time);

        int min = calendar.get(Calendar.MINUTE);
        String hourString = calendar.get(Calendar.HOUR_OF_DAY)+"";
        String minString = calendar.get(Calendar.MINUTE)+"";
        if(calendar.get(Calendar.HOUR_OF_DAY)<10){
            hourString = "0"+hourString;
        }
        if(calendar.get(Calendar.MINUTE)<10){
            minString = "0"+minString;
        }
        return hourString+":"+minString;
    }

    private String getTimeStringFormat(Long time) {
        long hours = time / (60 * 60 * 1000); //since both are ints, you get an int
        long minutes = time / (60 * 1000) % 60;
        String hour = hours+"";
        String min = minutes+"";
        if(hours<10){
            hour = "0"+hour;
        }
        if(minutes<10){
            min = "0"+min;
        }
        return hour+":"+min;
    }


    public class WifiStatusHistory implements Comparable {
        String ssidName;
        String connectionDate;
        String connectionTotalTime;
        String connectionFromTime;
        Long connectionFromTimeLong;
        String connectionToTime;
        Long connectionToTimeLong;


        public String getSsidName() {
            return ssidName;
        }

        public void setSsidName(String ssidName) {
            this.ssidName = ssidName;
        }

        public String getConnectionDate() {
            return connectionDate;
        }

        public void setConnectionDate(String connectionDate) {
            this.connectionDate = connectionDate;
        }

        public String getConnectionTotalTime() {
            return connectionTotalTime;
        }

        public void setConnectionTotalTime(String connectionTotalTime) {
            this.connectionTotalTime = connectionTotalTime;
        }

        public String getConnectionFromTime() {
            return connectionFromTime;
        }

        public void setConnectionFromTime(String connectionFromTime) {
            this.connectionFromTime = connectionFromTime;
        }

        public String getConnectionToTime() {
            return connectionToTime;
        }

        public void setConnectionToTime(String connectionToTime) {
            this.connectionToTime = connectionToTime;
        }

        public Long getConnectionFromTimeLong() {
            return connectionFromTimeLong;
        }

        public void setConnectionFromTimeLong(Long connectionFromTimeLong) {
            this.connectionFromTimeLong = connectionFromTimeLong;
        }

        public Long getConnectionToTimeLong() {
            return connectionToTimeLong;
        }

        public void setConnectionToTimeLong(Long connectionToTimeLong) {
            this.connectionToTimeLong = connectionToTimeLong;
        }

        @Override
        public int compareTo(Object o) {
            WifiStatusHistory wifiStatusHistoryToCompare = (WifiStatusHistory)o;
            if(wifiStatusHistoryToCompare.getConnectionFromTimeLong() < this.getConnectionFromTimeLong()){
                return -1;
            }
            if(wifiStatusHistoryToCompare.getConnectionFromTimeLong() > this.getConnectionFromTimeLong()){
                return 1;
            }
            return 0;
        }
    }
}


