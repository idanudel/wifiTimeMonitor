package com.example.idanm.wifitimemonitor.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ShowWifiMonitor extends AppCompatActivity {
    ShowWifiMonitorAdapter showWifiMonitorAdapter =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wifi_monitor);
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
        Integer wifiKey = (Integer)bd.get("wifiKey");
        ArrayList<WifiMonitorConnections> wifiMonitorConnections= new DbHelper(this).getWifiMonitorConnectionses(wifiKey.intValue());


        showWifiMonitorAdapter = new ShowWifiMonitorAdapter(this,
                R.layout.content_edit_wifi_monitor, organizeWifiMonitorConnections(wifiMonitorConnections));
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
                String formatString = "dd.MM.yy";
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
        return calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
    }

    private String getTimeStringFormat(Long time) {
        long hours = time / (60 * 60 * 1000); //since both are ints, you get an int
        long minutes = time / (60 * 1000) % 60;
        return hours+":"+minutes;
    }


    public class WifiStatusHistory {
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
    }
}


