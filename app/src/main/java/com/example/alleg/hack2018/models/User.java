package com.example.alleg.hack2018.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.utility.DBUtility;
import com.example.alleg.hack2018.utility.Queries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class User implements DatabaseModel {
    public String name;
    public String phoneNumber;
    public int salt;
    public int password;
    public boolean resident;
    private String id;

    // for firebase
    public User() {}

    // get this from the database
    User(String id, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + UserContract.TABLE_NAME
                + " WHERE " + UserContract._ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        cursor.moveToFirst();

        Log.d("searching for user", id);

        if (cursor.getCount() == 0) {
            //Can't find id
            //TODO
        }

        this.id = id;
        this.name = cursor.getString(cursor.getColumnIndex(UserContract.COLUMN_NAME_NAME));
        this.phoneNumber = cursor.getString(cursor.getColumnIndex(UserContract.COLUMN_NAME_PHONE_NUMBER));
        byte[] s = cursor.getBlob(cursor.getColumnIndex(UserContract.COLUMN_NAME_SALT));
        byte[] p = cursor.getBlob(cursor.getColumnIndex(UserContract.COLUMN_NAME_PASSWORD));

        this.salt = DBUtility.fromByteArray(s);
        this.password = DBUtility.fromByteArray(p);

        int res = cursor.getInt(cursor.getColumnIndex(UserContract.COLUMN_NAME_RESIDENT));
        this.resident = res == 1;
        cursor.close();
    }

    // from content values
    User(ContentValues values) {
        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case UserContract.COLUMN_NAME_PHONE_NUMBER:
                    this.phoneNumber = value.toString();
                    break;
                case UserContract.COLUMN_NAME_SALT:
                    this.salt = DBUtility.fromByteArray((byte[]) value);
                    break;
                case UserContract.COLUMN_NAME_NAME:
                    this.name = value.toString();
                    break;
                case UserContract.COLUMN_NAME_PASSWORD:
                    this.password = DBUtility.fromByteArray((byte[]) value);
                    break;
                case UserContract._ID:
                    this.id = value.toString();
                    break;
                case UserContract.COLUMN_NAME_RESIDENT:
                    this.resident = (int) value == 1;
                    break;
            }
        }
    }

    // get any incoming messages
    // where dest = this user object's id
    ArrayList<Message> getPrivateMessages(SQLiteDatabase db) {
        ArrayList<Message> arr = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + MessageContract.TABLE_NAME
                + " WHERE " + MessageContract.COLUMN_NAME_DESTINATION_ID + " = " + this.id;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});

        while(cursor.moveToNext()) {
            Message temp = new Message(cursor.getString(cursor.getColumnIndex(MessageContract._ID)), db);
            arr.add(temp);
        }

        cursor.close();
        return arr;
    }

    // get any messages where this user's id is the sender
    ArrayList<Message> getOutgoingMessages(SQLiteDatabase db) {
        ArrayList<Message> arr = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + MessageContract.TABLE_NAME
                + " WHERE " + MessageContract.COLUMN_NAME_USER_ID + " = " + this.id;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});

        while(cursor.moveToNext()) {
            Message temp = new Message(cursor.getString(cursor.getColumnIndex(MessageContract._ID)), db);
            arr.add(temp);
        }

        cursor.close();
        return arr;
    }

    // return a list of inventory
    // this means, return a list of objects with this's id, and then an item id, and a count
    // then, use the item id to grab Item objects using constructor
    ArrayList<Inventory> getItemsInInventory() {
        ArrayList<Inventory> arr = new ArrayList<>();

        // TODO Sahil - low priority
        // this will slightly more difficult than the others

        return arr;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        int res = 0;
        if (this.resident) {
            res = 1;
        }

        values.put(UserContract._ID, this.id);
        values.put(UserContract.COLUMN_NAME_NAME, this.name);
        values.put(UserContract.COLUMN_NAME_PASSWORD, DBUtility.toByteArray(this.password));
        values.put(UserContract.COLUMN_NAME_PHONE_NUMBER, this.phoneNumber);
        values.put(UserContract.COLUMN_NAME_RESIDENT, res);
        values.put(UserContract.COLUMN_NAME_SALT, DBUtility.toByteArray(this.salt));

        return values;
    }

    public String getID() {
        return this.id;
    }

    public String getTableName() {
        return UserContract.TABLE_NAME;
    }

    public String getCreateTable() {
        return Queries.getCreateTableStatement(this.getTableName(), UserContract.getTableFields());
    }
}
