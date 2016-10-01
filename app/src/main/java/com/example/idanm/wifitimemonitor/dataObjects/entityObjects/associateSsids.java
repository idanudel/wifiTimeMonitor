package com.example.idanm.wifitimemonitor.dataObjects.entityObjects;

/**
 * Created by idanm on 10/2/16.
 */
public class AssociateSsids {
    private int id;
    private String ssidName;
    private int wifiMonitorEntryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSsidName() {
        return ssidName;
    }

    public void setSsidName(String ssidName) {
        this.ssidName = ssidName;
    }

    public int getWifiMonitorEntryId() {
        return wifiMonitorEntryId;
    }

    public void setWifiMonitorEntryId(int wifiMonitorEntryId) {
        this.wifiMonitorEntryId = wifiMonitorEntryId;
    }
}
