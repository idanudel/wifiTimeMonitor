package com.example.idanm.wifitimemonitor.dataObjects.viewObjects;

/**
 * Created by idanm on 10/1/16.
 */
public class WifiMonitorEntry {

    private String entryName;
    private int wifiMonitorEntryId;
    private long connectionDate;
    private long connectedTime;

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public int getWifiMonitorEntryId() {
        return wifiMonitorEntryId;
    }

    public void setWifiMonitorEntryId(int wifiMonitorEntryId) {
        this.wifiMonitorEntryId = wifiMonitorEntryId;
    }

    public long getConnectionDate() {
        return connectionDate;
    }

    public void setConnectionDate(long connectionDate) {
        this.connectionDate = connectionDate;
    }

    public long getConnectedTime() {
        return connectedTime;
    }

    public void setConnectedTime(long connectedTime) {
        this.connectedTime = connectedTime;
    }
}
