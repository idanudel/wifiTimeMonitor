package com.example.idanm.wifitimemonitor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.AssociateSsids;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.OperationType;

import com.example.idanm.wifitimemonitor.db.DbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by idanm on 9/16/16.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


//        String action = intent.getAction();
//        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {
//            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
//            if (SupplicantState.isValidState(state)
//                    && state == SupplicantState.COMPLETED) {
//                String ssidName = getSsidName(context);
//                WifiStateHistory.recordConnectedSsid(ssidName);
//                ArrayList<Long> connectedKeys = checkConnectedToDesiredWifi(ssidName,context);
//                if(connectedKeys==null || connectedKeys.size()<1){
//                    return;
//                }
//                updateOnConnect(context,connectedKeys,ssidName);
//            }
////            if (SupplicantState.isValidState(state)
////                    && state == SupplicantState.DISCONNECTED) {
////                String ssidName = getSsidName(context);
////                ArrayList<Long> connectedKeys = checkConnectedToDesiredWifi(ssidName);
////                if(connectedKeys==null || connectedKeys.size()<1){
////                    return;
////                }
////                updateOnDisconnect(context,connectedKeys,ssidName);
////            }
//        }
//        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action))
//        {
//            WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            NetworkInfo.State state = networkInfo.getState();
//            if(state == NetworkInfo.State.DISCONNECTED)
//            {
//                if(manager.isWifiEnabled())
//                {
//                    String ssidName =  WifiStateHistory.getLastConnectedSsid();
//                    ArrayList<Long> connectedKeys = checkConnectedToDesiredWifi(ssidName,context);
//                    if(connectedKeys==null || connectedKeys.size()<1){
//                        return;
//                    }
//                    WifiStateHistory.recordConnectedSsid(null);
//                    updateOnDisconnect(context,connectedKeys,ssidName);
//
//                }
//            }
//        }
   }


}
