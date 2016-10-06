package com.example.idanm.wifitimemonitor.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class WifiStatusUpdater
{

    private static final String TAG = "WIFIMONITOR";
    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmMgr;
    private static final long FREQUENCY = 30000;

    public WifiStatusUpdater(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, BootBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        updateWifiStatus();
        setNextAlarm();
    }

    private void updateWifiStatus() {

    }

    public void setNextAlarm(){
        alarmMgr.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + FREQUENCY), pendingIntent);
    }

    private void stopAlarms(){
        alarmMgr.cancel(pendingIntent);
    }
}