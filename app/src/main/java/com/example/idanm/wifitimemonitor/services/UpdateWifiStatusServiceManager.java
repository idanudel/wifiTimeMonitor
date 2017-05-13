package com.example.idanm.wifitimemonitor.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.idanm.wifitimemonitor.utils.CONST;

/**
 * Created by idanm on 5/12/17.
 */
public class UpdateWifiStatusServiceManager {


    private static UpdateWifiStatusServiceManager updateWifiStatusServiceManager = null;

    private  UpdateWifiStatusServiceManager(){

    }

    public static UpdateWifiStatusServiceManager getInstance(){
        if(updateWifiStatusServiceManager == null)
        {
            updateWifiStatusServiceManager = new UpdateWifiStatusServiceManager();
        }
        return updateWifiStatusServiceManager;
    }

    public void startUpdateWifiStatusService(Context context){
        if(isMyServiceRunning(UpdateWifiStatusService.class,context)){
            Log.i(CONST.TAG,"UpdateWifiStatusService already running!");
            return;
        }
        Log.i(CONST.TAG,"starting updateWifiStatusService Intent");
        Intent startServiceIntent = new Intent(context, UpdateWifiStatusService.class);
        context.startService(startServiceIntent);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
