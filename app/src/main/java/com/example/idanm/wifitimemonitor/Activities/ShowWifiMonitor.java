package com.example.idanm.wifitimemonitor.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.idanm.wifitimemonitor.R;
import com.example.idanm.wifitimemonitor.db.DbEntriesParams;
import com.example.idanm.wifitimemonitor.db.DbHelper;

import java.text.DateFormat;
import java.util.Date;

public class ShowWifiMonitor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wifi_monitor);

        TableLayout tableLayout = (TableLayout)findViewById(R.id.wifiMonitorData);

        Cursor cursor = new DbHelper(this).getWifiMonitor();
        if(cursor==null) return;
        addHeadersRowToTable(tableLayout);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            addDataRowsToTable(tableLayout,cursor);
        }

    }

    private void addHeadersRowToTable(TableLayout tableLayout) {

        String [] columnNames = {"ID","Time","Status","Wifi Name"};
        for(String columnName: columnNames){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
            TextView columns = new TextView(this);
            columns.setText("ID");
            columns.setText(columnName);
            tableRow.addView(columns);
            tableLayout.addView(tableRow);
        }
    }

    private void addDataRowsToTable(TableLayout tableLayout, Cursor cursor) {
        String[] columnNames = cursor.getColumnNames();
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(lp);
        for(String cursorColumnName:columnNames){
            final TextView columns = new TextView(this);
            int index = cursor.getColumnIndex(cursorColumnName);
            if(cursor.getType(cursor.getColumnIndex(cursorColumnName))==1){//int
                String text = cursor.getLong(index)+"";
                if(DbEntriesParams.COLUMN_NAME_TIME.equals(cursorColumnName)){
                    Date date = new Date(cursor.getLong(index));
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    text = dateFormat.format(date);
                }
                columns.setText(text);
            }

            if(cursor.getType(cursor.getColumnIndex(cursorColumnName))==3){//String
                columns.setText(cursor.getString(index));
            }
            tableRow.addView(columns);

        }
        tableLayout.addView(tableRow);

    }

}
