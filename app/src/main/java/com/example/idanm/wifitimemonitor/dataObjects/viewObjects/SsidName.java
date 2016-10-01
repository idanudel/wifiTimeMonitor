package com.example.idanm.wifitimemonitor.dataObjects.viewObjects;

/**
 * Created by idanm on 9/29/16.
 */
public class SsidName {
    private String ssidName;
    private boolean selected = false;

    public SsidName(String ssidName, boolean selected) {
        this.ssidName = ssidName;
        this.selected = selected;
    }

    public String getSsidName() {
        return ssidName;
    }

    public void setSsidName(String ssidName) {
        this.ssidName = ssidName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
