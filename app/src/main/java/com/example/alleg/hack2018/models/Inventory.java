package com.example.alleg.hack2018.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.MessageContract;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Inventory implements Serializable {

    public String id;
    public String userId;
    public String item;
    public int count;

    // get from database
    public Inventory(String id, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + InventoryContract.Inventory.TABLE_NAME
                + " WHERE " + InventoryContract.Inventory._ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            //Can't find id
            //TODO
        }

        this.id = id;
        this.userId = cursor.getString(cursor.getColumnIndex(InventoryContract.Inventory.COLUMN_NAME_USER_ID));
        this.item = cursor.getString(cursor.getColumnIndex(InventoryContract.Inventory.COLUMN_NAME_ITEM));
        this.count = cursor.getInt(cursor.getColumnIndex(InventoryContract.Inventory.COLUMN_NAME_COUNT));
    }

    public Inventory(ContentValues values){
        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case InventoryContract.Inventory.COLUMN_NAME_ITEM:
                    this.item = value.toString();
                    break;
                case InventoryContract.Inventory.COLUMN_NAME_COUNT:
                    this.count = (int) value;
                    break;
                case InventoryContract.Inventory.COLUMN_NAME_USER_ID:
                    this.userId = value.toString();
                    break;
                case InventoryContract.Inventory._ID:
                    this.id = value.toString();
                    break;
            }
        }
    }

}
