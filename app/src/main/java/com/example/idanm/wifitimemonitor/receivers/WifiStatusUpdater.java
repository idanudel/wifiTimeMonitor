package com.example.idanm.wifitimemonitor.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.AssociateSsids;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.OperationType;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorConnections;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorEntryEntity;
import com.example.idanm.wifitimemonitor.db.DbHelper;
import com.example.idanm.wifitimemonitor.utils.CONST;

import java.util.ArrayList;

public class WifiStatusUpdater
{
    Context context;
    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmMgr;
    private static final long FREQUENCY = 60000;

    public WifiStatusUpdater(Context context) {
        this.context = context;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, BootBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        try {
            updateWifiStatus();
        } catch (Exception e) {
            Log.e(CONST.TAG,"Failed to update wifiStatus",e);
        }
        setNextAlarm();
    }

    private void updateWifiStatus() {
        boolean isNeedToUpdate = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String ssid  = info.getSSID();
        DbHelper dbHelper = new DbHelper(context);
        ArrayList<WifiMonitorEntryEntity> wifiMonitorEntryEntities = dbHelper.getWifiAllMonitorEntries();
        if(wifiMonitorEntryEntities==null || wifiMonitorEntryEntities.isEmpty()){
            Log.i(CONST.TAG,"No wifiMonitor entries");
            return;
        }
        ArrayList<Integer> wifiMonitorEntryEntityIds = new ArrayList<>();
        for(WifiMonitorEntryEntity wifiMonitorEntryEntity:wifiMonitorEntryEntities){
            ArrayList<AssociateSsids> associateSsids = wifiMonitorEntryEntity.getAssociateSsidses();
            if(associateSsids==null || associateSsids.isEmpty())continue;
            for(AssociateSsids associateSsid:associateSsids){
                if(associateSsid==null)continue;
                if(isEqualsSsid(associateSsid.getSsidName(),ssid)){
                    wifiMonitorEntryEntityIds.add(wifiMonitorEntryEntity.getId());
                }
            }
        }
        if(wifiMonitorEntryEntityIds.isEmpty())return;

        for(Integer wifiMonitorEntryEntityId:wifiMonitorEntryEntityIds){
            ArrayList<WifiMonitorConnections>  wifiMonitorConnections = dbHelper.getWifiTodayMonitorConnectionses(wifiMonitorEntryEntityId);
            if(wifiMonitorConnections==null || wifiMonitorConnections.isEmpty()||wifiMonitorConnections.size()<2){
                ArrayList<Long> wifiMonitorIds = new ArrayList<>();
                wifiMonitorIds.add(wifiMonitorEntryEntityId.longValue());
                dbHelper.insertWifiStatus(wifiMonitorIds,ssid, OperationType.CONNECT);
                return;
            }
            WifiMonitorConnections wifiMonitorConnectionToUpdate =null;
            for(WifiMonitorConnections wifiMonitorConnection :wifiMonitorConnections){
                if(wifiMonitorConnectionToUpdate ==null || wifiMonitorConnectionToUpdate.getDate().getTime()<wifiMonitorConnection.getDate().getTime()){
                    wifiMonitorConnectionToUpdate = wifiMonitorConnection;
                }
            }
            dbHelper.updateWifiStatus(wifiMonitorConnectionToUpdate);
            isNeedToUpdate = true;
        }

        if(isNeedToUpdate) {
            Intent intent = new Intent(CONST.UPDATE_UI_ACTION);
            context.sendBroadcast(intent);
        }


    }

    private boolean isEqualsSsid(String ssidName, String ssid) {
        if(ssidName ==null){
            return false;
        }
        if(ssid == null ){
            return false;
        }
        return ssid.replace("\"","").equals(ssidName.replace("\"",""));
    }

    public void setNextAlarm(){
        alarmMgr.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + FREQUENCY), pendingIntent);
    }

    private void stopAlarms(){
        alarmMgr.cancel(pendingIntent);
    }
    public boolean isAlarmUp(){
        return (PendingIntent.getBroadcast(context, 0,
                new Intent(context, BootBroadcastReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }
}