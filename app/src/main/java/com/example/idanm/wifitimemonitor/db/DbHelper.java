package com.example.idanm.wifitimemonitor.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.AssociateSsids;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.OperationType;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorConnections;
import com.example.idanm.wifitimemonitor.dataObjects.entityObjects.WifiMonitorEntryEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by idanm on 9/16/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
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

    public void  clearAll() {
        onUpgrade(getWritableDatabase(),0,0);
    }
    public boolean deleteWifiEntry(int wifiEnteryKey) {
        getWritableDatabase().delete(DbEntriesParams.TABLE_NAME_WIFIMONITOR, DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID+"=?", new String[]{Integer.toString(wifiEnteryKey)});
        getWritableDatabase().delete(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY_SSIDS, DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_WIFIMONITOR_ID+"=?", new String[]{Integer.toString(wifiEnteryKey)});
        getWritableDatabase().delete(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY, DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID+"=?", new String[]{Integer.toString(wifiEnteryKey)});
        return true;

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

    public boolean updateWifiStatus(WifiMonitorConnections wifiMonitorConnections ){
        if(wifiMonitorConnections.getSsidName()==null || wifiMonitorConnections.getSsidName().isEmpty())return false;

            ContentValues values = new ContentValues();
            values.put(DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID, wifiMonitorConnections.getWifiMonitorEntryId());
            values.put(DbEntriesParams.COLUMN_NAME_TIME, Calendar.getInstance().getTimeInMillis());
            values.put(DbEntriesParams.COLUMN_OPERATION,OperationType.CONNECT.name());
            values.put(DbEntriesParams.COLUMN_SSID_NAME,wifiMonitorConnections.getSsidName());

            String selection = DbEntriesParams.COLUMN_NAME_ID + " = ? ";
            String [] selectionArgs = { wifiMonitorConnections.getId()+""};
            getWritableDatabase().update(DbEntriesParams.TABLE_NAME_WIFIMONITOR,values,selection,selectionArgs);


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
    public boolean updateWifiTimeMonitorSetting(String wifiTimeMonitorName,long wifiMonitorEntryId,ArrayList<String> ssidNames){
        if(ssidNames==null || ssidNames.size()<1||wifiTimeMonitorName==null || wifiTimeMonitorName.isEmpty())return false;

        String selection = DbEntriesParams.COLUMN_NAME_ID + " = ? ";
        String [] selectionArgs = { wifiMonitorEntryId+""};
        ContentValues wifiMonitorEntry = new ContentValues();
        wifiMonitorEntry.put(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_NAME, wifiTimeMonitorName);
        getWritableDatabase().update(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY,wifiMonitorEntry,selection,selectionArgs);
        getWritableDatabase().delete(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY_SSIDS, DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_WIFIMONITOR_ID + "=" + wifiMonitorEntryId, null);

        for(String ssidName:ssidNames){
            ContentValues values = new ContentValues();
            values.put(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_SSIDS_NAME, ssidName);
            values.put(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_WIFIMONITOR_ID, wifiMonitorEntryId);
            getWritableDatabase().insert(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY_SSIDS,null,values);
        };
        return true;
    }

    public ArrayList<WifiMonitorEntryEntity> getWifiAllMonitorEntries(){
        ArrayList<WifiMonitorEntryEntity> wifiMonitorEntryEntities = new ArrayList<>();
        String[] projection = {
                DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID,
                DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_NAME
        };
        String sortOrder = DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID + " ASC";

        Cursor cursor = getReadableDatabase().query(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY, projection, null, null, null, null, sortOrder);

        if (cursor.moveToFirst()){
            do{
                WifiMonitorEntryEntity wifiMonitorEntryEntity = new WifiMonitorEntryEntity();
                int id = cursor.getInt(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID));
                String wifiMonitorName = cursor.getString(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_NAME));
                wifiMonitorEntryEntity.setId(id);
                wifiMonitorEntryEntity.setWifiMonitorEntryName(wifiMonitorName);
                wifiMonitorEntryEntity.setAssociateSsidses(getAssociateSsidses(id));
                wifiMonitorEntryEntity.setWifiMonitorConnectionses(getWifiMonitorConnectionses(id));
                wifiMonitorEntryEntities.add(wifiMonitorEntryEntity);
            }while(cursor.moveToNext());
        }
        cursor.close();


        return wifiMonitorEntryEntities;
    }

    public WifiMonitorEntryEntity getWifiMonitorEntries(Integer wifiMonitorEntryKey){
        ArrayList<WifiMonitorEntryEntity> wifiMonitorEntryEntities = new ArrayList<>();
        String[] projection = {
                DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID,
                DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_NAME
        };
        String sortOrder = DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID + " ASC";

        String selection = DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID + " = ? " ;
        String [] selectionArgs = { wifiMonitorEntryKey+""};

        Cursor cursor = getReadableDatabase().query(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY, projection, selection, selectionArgs, null, null, sortOrder);

        if (cursor.moveToFirst()){
            do{
                WifiMonitorEntryEntity wifiMonitorEntryEntity = new WifiMonitorEntryEntity();
                int id = cursor.getInt(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_ID));
                String wifiMonitorName = cursor.getString(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_NAME));
                wifiMonitorEntryEntity.setId(id);
                wifiMonitorEntryEntity.setWifiMonitorEntryName(wifiMonitorName);
                wifiMonitorEntryEntity.setAssociateSsidses(getAssociateSsidses(id));
                wifiMonitorEntryEntity.setWifiMonitorConnectionses(getWifiMonitorConnectionses(id));
                wifiMonitorEntryEntities.add(wifiMonitorEntryEntity);
            }while(cursor.moveToNext());
        }
        cursor.close();

        if(wifiMonitorEntryEntities!=null && wifiMonitorEntryEntities.size()>0) {
            return wifiMonitorEntryEntities.get(0);
        }
        return null;
    }


    public ArrayList<WifiMonitorConnections> getWifiTodayMonitorConnectionses(int wifiKey){
        long from = getStartOfDay(new Date());
        long to = getEndOfDay(new Date());
        return getWifiTodayMonitorConnectionses(wifiKey,from,to);
    }
    public ArrayList<WifiMonitorConnections> getWifiMonitorConnectionses(int wifiKey){
        return getWifiTodayMonitorConnectionses(wifiKey,0,999999999999999999L);
    }
    public ArrayList<WifiMonitorConnections> getWifiTodayMonitorConnectionses(int wifiKey,long from,long to){
        ArrayList<WifiMonitorConnections> wifiMonitorConnectionses = new ArrayList<>();
        String[] projection = {
                DbEntriesParams.COLUMN_NAME_ID,
                DbEntriesParams.COLUMN_NAME_TIME,
                DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID,
                DbEntriesParams.COLUMN_OPERATION,
                DbEntriesParams.COLUMN_SSID_NAME
        };
        String selection = DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID + " = ? AND "+  DbEntriesParams.COLUMN_NAME_TIME+" <= ? AND "+  DbEntriesParams.COLUMN_NAME_TIME+" >= ?" ;
        String [] selectionArgs = { wifiKey +"",to+"",from+""};
        String sortOrder = DbEntriesParams.COLUMN_NAME_TIME + " DESC";
        Cursor cursor = getReadableDatabase().query(DbEntriesParams.TABLE_NAME_WIFIMONITOR, projection, selection, selectionArgs, null, null, sortOrder);

        if (cursor.moveToFirst()){
            do{
                WifiMonitorConnections wifiMonitorConnections = new WifiMonitorConnections();
                int id = cursor.getInt(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_ID));
                Long time = cursor.getLong(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_TIME));
                int wifiMonitorEntryId = cursor.getInt(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFI_MONITOR_ID));
                String op = cursor.getString(cursor.getColumnIndex(DbEntriesParams.COLUMN_OPERATION));
                String ssidName = cursor.getString(cursor.getColumnIndex(DbEntriesParams.COLUMN_SSID_NAME));
                wifiMonitorConnections.setId(id);
                wifiMonitorConnections.setWifiMonitorEntryId(wifiMonitorEntryId);
                wifiMonitorConnections.setDate(new Date(time));
                wifiMonitorConnections.setSsidName(ssidName);
                wifiMonitorConnections.setOp(op);
                wifiMonitorConnectionses.add(wifiMonitorConnections);

            }while(cursor.moveToNext());
        }

        return wifiMonitorConnectionses;
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


    public ArrayList<AssociateSsids> getAssociateSsidses(int wifiMonitorEntryId) {
        String[] projection = {
                DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_SSIDS_ID,
                DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_SSIDS_NAME,
                DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_WIFIMONITOR_ID
        };
        String selection = null;
        String[] selectionArgs = null;
        if(wifiMonitorEntryId>0) {
            selection = DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_WIFIMONITOR_ID + " = ?";
            selectionArgs = new String [1];
            selectionArgs[0] = wifiMonitorEntryId + "";
        }
        String sortOrder = DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_SSIDS_NAME + " DESC";
        Cursor cursor = getReadableDatabase().query(DbEntriesParams.TABLE_NAME_WIFIMONITOR_ENTRY_SSIDS, projection, selection, selectionArgs, null, null, sortOrder);
        ArrayList<AssociateSsids> associateSsidses = new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                AssociateSsids associateSsids = new AssociateSsids();
                int id = cursor.getInt(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_SSIDS_ID));
                String ssidName = cursor.getString(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_SSIDS_NAME));
                int associateSsidId = cursor.getInt(cursor.getColumnIndex(DbEntriesParams.COLUMN_NAME_WIFIMONITOR_ENTRY_WIFIMONITOR_ID));
                associateSsids.setId(id);
                associateSsids.setSsidName(ssidName);
                associateSsids.setWifiMonitorEntryId(associateSsidId);
                associateSsidses.add(associateSsids);

            }while(cursor.moveToNext());
        }

        return associateSsidses;
    }



    public long getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        calendar.set(Calendar.MINUTE, 0); // set minutes to zero
        calendar.set(Calendar.SECOND, 0); //set seconds to zero
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public long getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

}