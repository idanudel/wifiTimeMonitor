package com.example.idanm.wifitimemonitor.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.idanm.wifitimemonitor.DataObjects.OperationType;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by idanm on 9/16/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FeedReader.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbEntriesParams.SQL_CREATE_ENTRIES_WIFIMONITOR);
        db.execSQL(DbEntriesParams.SQL_CREATE_ENTRIES_WIFIMONITOR_ENTRY);
        db.execSQL(DbEntriesParams.SQL_CREATE_ENTRIES_WIFIMONITOR_ENTRY_SSIDS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbEntriesParams.SQL_DELETE_ENTRIES_WIFIMONITOR);
        db.execSQL(DbEntriesParams.SQL_DELETE_ENTRIES_WIFIMONITOR_ENTRY_SSIDS);
        db.execSQL(DbEntriesParams.SQL_DELETE_ENTRIES_WIFIMONITOR_ENTRY);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertWifiStatus(ArrayList<Long> wifiKeys, String ssidName, OperationType operationType){
        if(wifiKeys==null || wifiKeys.size()<1||operationType==null || null == ssidName || ssidName.isEmpty())return false;

        for(Long wifiKey:wifiKeys){
            ContentValues values = new ContentValues();
            values.put(DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID, wifiKey);
            values.put(DbEntriesParams.COLUMN_NAME_TIME, Calendar.getInstance().getTimeInMillis());
            values.put(DbEntriesParams.COLUMN_OPERATION,operationType.name());
            values.put(DbEntriesParams.COLUMN_SSID_NAME,ssidName);
            getWritableDatabase().insert(DbEntriesParams.TABLE_NAME_WIFIMONITOR,null,values);
        }
        return true;
    }

    public boolean insertWifiTimeMonitorSetting(String wifiTimeMonitorName,ArrayList<String> ssidNames){
        if(ssidNames==null || ssidNames.size()<1||wifiTimeMonitorName==null || wifiTimeMonitorName.isEmpty())return false;

        ContentValues wifiMonitorEntry = new ContentValues();
        wifiMonitorEntry.put(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_NAME, wifiTimeMonitorName);
        long id = getWritableDatabase().insert(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY,null,wifiMonitorEntry);

        for(String ssidName:ssidNames){
            ContentValues values = new ContentValues();
            values.put(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_SSIDS_NAME, ssidName);
            values.put(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_WIFIMONITOR_ID, id);
            getWritableDatabase().insert(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY_SSIDS,null,values);
        };
        return true;
    }




    public Cursor getWifiMonitorByWifiKey(Long wifiKey){
        String[] projection = {
                DbEntriesParams.COLUMN_NAME_ID,
                DbEntriesParams.COLUMN_NAME_TIME,
                DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID,
                DbEntriesParams.COLUMN_OPERATION,
                DbEntriesParams.COLUMN_SSID_NAME
        };
        String selection = DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID + " = ?";
        String [] selectionArgs = { wifiKey.toString() };
        String sortOrder = DbEntriesParams.COLUMN_NAME_TIME + " DESC";
        return getReadableDatabase().query(DbEntriesParams.TABLE_NAME_WIFIMONITOR,projection,selection,selectionArgs,null,null,sortOrder);
     }
    public Cursor getWifiMonitor(){
        String[] projection = {
                DbEntriesParams.COLUMN_NAME_ID,
                DbEntriesParams.COLUMN_NAME_TIME,
                DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID,
                DbEntriesParams.COLUMN_OPERATION,
                DbEntriesParams.COLUMN_SSID_NAME
        };
        String sortOrder = DbEntriesParams.COLUMN_NAME_TIME + " ASC";
        return getReadableDatabase().query(DbEntriesParams.TABLE_NAME_WIFIMONITOR,projection,null,null,null,null,sortOrder);
    }

}