package com.example.idanm.wifitimemonitor.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.example.idanm.wifitimemonitor.receivers.UpdateWifiStatusReceiver;
import com.example.idanm.wifitimemonitor.utils.CONST;

/**
 * Created by idanm on 5/12/17.
 */
public class UpdateWifiStatusService extends Service {

    public UpdateWifiStatusService(){
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(CONST.TAG,"UpdateWifiStatusService onStartCommand");
        AlarmManager alarmManager=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, UpdateWifiStatusReceiver.class), 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),10000,
                pendingIntent);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
