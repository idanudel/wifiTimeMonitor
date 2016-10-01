package com.example.idanm.wifitimemonitor.dataObjects.entityObjects;



import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by idanm on 10/2/16.
 */
public class WifiMonitorEntryEntity {
    private int id;
    private String wifiMonitorEntryName;
    private ArrayList<WifiMonitorConnections> wifiMonitorConnectionses = new ArrayList<>();
    private ArrayList<AssociateSsids> associateSsidses = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWifiMonitorEntryName() {
        return wifiMonitorEntryName;
    }

    public void setWifiMonitorEntryName(String wifiMonitorEntryName) {
        this.wifiMonitorEntryName = wifiMonitorEntryName;
    }

    public ArrayList<WifiMonitorConnections> getWifiMonitorConnectionses() {
        return wifiMonitorConnectionses;
    }

    public void setWifiMonitorConnectionses(ArrayList<WifiMonitorConnections> wifiMonitorConnectionses) {
        this.wifiMonitorConnectionses = wifiMonitorConnectionses;
    }

    public ArrayList<AssociateSsids> getAssociateSsidses() {
        return associateSsidses;
    }

    public void setAssociateSsidses(ArrayList<AssociateSsids> associateSsidses) {
        this.associateSsidses = associateSsidses;
    }


}
