package com.example.idanm.wifitimemonitor.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.idanm.wifitimemonitor.utils.CONST;

/**
 * Created by idanm on 10/2/16.
 */
public class UpdateWifiStatusReceiver extends BroadcastReceiver {
      @Override
    public void onReceive(Context context, Intent intent) {
          Log.i(CONST.TAG,"WifiStatusUpdater Start");
          WifiStatusUpdater.getInstance().updateWifiStatus(context);
          Log.i(CONST.TAG,"WifiStatusUpdater Finish");
    }



}

