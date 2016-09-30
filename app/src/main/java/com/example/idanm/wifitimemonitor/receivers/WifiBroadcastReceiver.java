package com.example.idanm.wifitimemonitor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.idanm.wifitimemonitor.DataObjects.OperationType;
import com.example.idanm.wifitimemonitor.Utils.WifiStateHistory;
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
        String action = intent.getAction();
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (SupplicantState.isValidState(state)
                    && state == SupplicantState.COMPLETED) {
                String ssidName = getSsidName(context);
                WifiStateHistory.recordConnectedSsid(ssidName);
                ArrayList<Long> connectedKeys = checkConnectedToDesiredWifi(ssidName);
                if(connectedKeys==null || connectedKeys.size()<1){
                    return;
                }
                updateOnConnect(context,connectedKeys,ssidName);
            }
//            if (SupplicantState.isValidState(state)
//                    && state == SupplicantState.DISCONNECTED) {
//                String ssidName = getSsidName(context);
//                ArrayList<Long> connectedKeys = checkConnectedToDesiredWifi(ssidName);
//                if(connectedKeys==null || connectedKeys.size()<1){
//                    return;
//                }
//                updateOnDisconnect(context,connectedKeys,ssidName);
//            }
        }
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
        {
            WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = networkInfo.getState();
            if(state == NetworkInfo.State.DISCONNECTED)
            {
                if(manager.isWifiEnabled())
                {
                    String ssidName =  WifiStateHistory.getLastConnectedSsid();
                    ArrayList<Long> connectedKeys = checkConnectedToDesiredWifi(ssidName);
                    if(connectedKeys==null || connectedKeys.size()<1){
                        return;
                    }
                    WifiStateHistory.recordConnectedSsid(null);
                    updateOnDisconnect(context,connectedKeys,ssidName);

                }
            }
        }
    }

    private String getSsidName(Context context) {
        WifiManager wifiManager =
                (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wifiManager.getConnectionInfo();
        if(wifi==null) return null;
        return wifi.getSSID();
    }

    private void updateOnConnect(Context context , ArrayList<Long> connectedKeys, String ssidName) {
        new DbHelper(context).insertWifiStatus(connectedKeys,ssidName, OperationType.CONNECT);
    }
    private void updateOnDisconnect(Context context , ArrayList<Long> connectedKeys, String ssidName) {
        new DbHelper(context).insertWifiStatus(connectedKeys,ssidName, OperationType.DICONNECT);
    }

    private ArrayList<Long> checkConnectedToDesiredWifi(String ssid) {
       if (ssid==null) {
            return null;
        }
        Map<Long,ArrayList<String>> wifiNames = getWifiMap();
        Set<Long> wifiNamesKeys = wifiNames.keySet();
        ArrayList<Long> wifiKeysToReturn = new ArrayList<>();
        for(Long wifiKey :wifiNamesKeys){
            ArrayList<String> ssidNames = wifiNames.get(wifiKey);
            for(String name:ssidNames){
                if(ssid.equals(name))wifiKeysToReturn.add(wifiKey);
            }
        }

        return wifiKeysToReturn;
    }

    private Map<Long, ArrayList<String>> getWifiMap() {
        Map<Long, ArrayList<String>> wifiMap = new HashMap<>();
        ArrayList<String> wifiSsids = new ArrayList<String>();
        wifiSsids.add("Mor");
        wifiSsids.add("\"Mor\"");
        wifiSsids.add("Mor Test");
        wifiSsids.add("\"Mor Test\"");
        wifiMap.put(123L,wifiSsids);
        return wifiMap;

    }
}
