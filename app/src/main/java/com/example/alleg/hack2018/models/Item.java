package com.example.alleg.hack2018.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.utility.Queries;
import com.google.firebase.database.Exclude;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Item implements DatabaseModel {

    private String id;
    public String name;
    public boolean perishable;
    public int importance;

    // for firebase
    public Item() {}

    // get from db
    Item(String id, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + ItemContract.TABLE_NAME
                + " WHERE " + ItemContract._ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            //Can't find id
            //TODO
        }

        this.id = id;
        this.name = cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_NAME_NAME));
        this.importance = cursor.getInt(cursor.getColumnIndex(ItemContract.COLUMN_NAME_IMPORTANCE));
        int perish = cursor.getInt(cursor.getColumnIndex(ItemContract.COLUMN_NAME_PERISHABLE));
        this.perishable = perish == 1;
        cursor.close();
    }

    Item(ContentValues values){
        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case ItemContract.COLUMN_NAME_NAME:
                    this.name = value.toString();
                    break;
                case ItemContract.COLUMN_NAME_PERISHABLE:
                    this.perishable = (int) value == 1;
                    break;
                case ItemContract.COLUMN_NAME_IMPORTANCE:
                    this.importance = (int) value;
                    break;
                case ItemContract._ID:
                    this.id = value.toString();
                    break;
            }
        }
    }

    @Exclude
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(ItemContract._ID, this.id);
        values.put(ItemContract.COLUMN_NAME_IMPORTANCE, this.importance);
        values.put(ItemContract.COLUMN_NAME_NAME, this.name);
        values.put(ItemContract.COLUMN_NAME_PERISHABLE, this.perishable);

        return values;
    }

    @Exclude
    public String getID() {
        return this.id;
    }

    @Exclude
    public String getTableName() {
        return ItemContract.TABLE_NAME;
    }

    @Exclude
    public String getCreateTable() {
        return Queries.getCreateTableStatement(this.getTableName(), ItemContract.getTableFields());
    }

    @Exclude
    @Override
    public String getDropTable() {
        return DatabaseModel.super.getDropTable();
    }
}
