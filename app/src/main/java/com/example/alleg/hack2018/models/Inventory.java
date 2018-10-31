package com.example.alleg.hack2018.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Inventory implements DatabaseModel {

    public String id;
    public String userId;
    public String item;
    public int count;

    // for firebase
    public Inventory() {}

    // get from database
    public Inventory(String id, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + InventoryContract.TABLE_NAME
                + " WHERE " + InventoryContract._ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            //Can't find id
            //TODO
        }

        this.id = id;
        this.userId = cursor.getString(cursor.getColumnIndex(InventoryContract.COLUMN_NAME_USER_ID));
        this.item = cursor.getString(cursor.getColumnIndex(InventoryContract.COLUMN_NAME_ITEM));
        this.count = cursor.getInt(cursor.getColumnIndex(InventoryContract.COLUMN_NAME_COUNT));
        cursor.close();
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
                case InventoryContract.COLUMN_NAME_ITEM:
                    this.item = value.toString();
                    break;
                case InventoryContract.COLUMN_NAME_COUNT:
                    this.count = (int) value;
                    break;
                case InventoryContract.COLUMN_NAME_USER_ID:
                    this.userId = value.toString();
                    break;
                case InventoryContract._ID:
                    this.id = value.toString();
                    break;
            }
        }
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(InventoryContract._ID, this.id);
        values.put(InventoryContract.COLUMN_NAME_COUNT, this.count);
        values.put(InventoryContract.COLUMN_NAME_ITEM, this.item);
        values.put(InventoryContract.COLUMN_NAME_USER_ID, this.userId);

        return values;
    }

    public String getID() {
        return this.id;
    }
}
