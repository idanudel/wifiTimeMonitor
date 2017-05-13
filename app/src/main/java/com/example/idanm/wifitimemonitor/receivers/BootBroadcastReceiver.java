package com.example.idanm.wifitimemonitor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.idanm.wifitimemonitor.services.UpdateWifiStatusServiceManager;
import com.example.idanm.wifitimemonitor.utils.CONST;

/**
 * Created by idanm on 10/5/16.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(CONST.TAG, "ON BOOT CALL START");
        UpdateWifiStatusServiceManager.getInstance().startUpdateWifiStatusService(context);
        Log.i(CONST.TAG, "ON BOOT CALL FINISH");
    }
}