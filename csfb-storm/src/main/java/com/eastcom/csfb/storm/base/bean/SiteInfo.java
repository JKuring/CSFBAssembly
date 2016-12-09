package com.eastcom.csfb.storm.base.bean;

import com.eastcom.csfb.data.DbKeys;

import java.util.ArrayList;

public class SiteInfo {

    public static final SiteInfo emptySiteInfo = new SiteInfo();

    private int lac = 0;

    private int cellId = 0;

    private String vendor;

    private String sd = DbKeys.unknown;

    private String grid;

    private ArrayList<String> hotspots;

    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public ArrayList<String> getHotspots() {
        return hotspots;
    }

    public void setHotspots(ArrayList<String> hotspots) {
        this.hotspots = hotspots;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

}
