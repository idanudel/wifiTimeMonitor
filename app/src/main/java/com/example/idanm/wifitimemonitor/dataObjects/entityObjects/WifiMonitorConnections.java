package com.example.idanm.wifitimemonitor.dataObjects.entityObjects;

import java.util.Date;

/**
 * Created by idanm on 10/2/16.
 */
public class WifiMonitorConnections {
    private int id;
    private String ssidName;
    private String op;
    private Date date;
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

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getWifiMonitorEntryId() {
        return wifiMonitorEntryId;
    }

    public void setWifiMonitorEntryId(int wifiMonitorEntryId) {
        this.wifiMonitorEntryId = wifiMonitorEntryId;
    }
}
