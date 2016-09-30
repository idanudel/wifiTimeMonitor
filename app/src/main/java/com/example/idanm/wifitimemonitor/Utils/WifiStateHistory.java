package com.example.idanm.wifitimemonitor.Utils;

import android.net.ConnectivityManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by idanm on 9/16/16.
 */
public class WifiStateHistory
{
    private static String lastConnectedSsid = null;
    private static Date lastBroadcast = null;
    private static String lastBroadcastSsid = null;

    public static boolean notBroadcastInMinutes(String ssid, int minutes)
    {
        if(ssid != null && ssid.equals(lastBroadcastSsid))
        {
            long timeSince = (new Date()).getTime() - lastBroadcast.getTime();

            if(timeSince < TimeUnit.MINUTES.toMillis(minutes))
            {
                return false;
            }
        }

        return true;
    }

    public static void recordBroadcastNow(String ssid)
    {
        lastBroadcast = new Date();
        lastBroadcastSsid = ssid;
    }

    public static void recordConnectedSsid(String ssid)
    {
        lastConnectedSsid = ssid;
    }

    public static String getLastConnectedSsid()
    {
        return lastConnectedSsid;
    }


}
