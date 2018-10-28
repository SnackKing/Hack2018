package com.example.alleg.hack2018.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Message implements Serializable {
    public String id; // id of this record
    public String senderId; // id of sender
    public String recipId; // id of intended recipient
    public String msg; // message
    public int time; //time of message

    // pull from db
    public Message(String id, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + MessageContract.Message.TABLE_NAME
                + " WHERE " + MessageContract.Message._ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            //Can't find id
            //TODO
        }

        this.id = id;
        this.senderId = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_USER_ID));
        this.recipId = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_DESTINATION_ID));
        this.msg = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_MESSAGE));
        this.time = cursor.getInt(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_TIME));
        cursor.close();
    }

    public Message(ContentValues values){
        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case MessageContract.Message.COLUMN_NAME_MESSAGE:
                    this.msg = value.toString();
                    break;
                case MessageContract.Message.COLUMN_NAME_DESTINATION_ID:
                    this.recipId = value.toString();
                    break;
                case MessageContract.Message.COLUMN_NAME_USER_ID:
                    this.senderId = value.toString();
                    break;
                case MessageContract.Message._ID:
                    this.id = value.toString();
                    break;
                case MessageContract.Message.COLUMN_NAME_TIME:
                    this.time = (int) value;
                    break;
            }
        }
    }

    public static ArrayList<Message> getPublicMessages(SQLiteDatabase db) {
        ArrayList<Message> arr = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + MessageContract.Message.TABLE_NAME
                + " WHERE " + MessageContract.Message.COLUMN_NAME_DESTINATION_ID + " = -1";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        cursor.moveToFirst();

        for(int i = 0; i < cursor.getCount(); i++) {
            Message temp = new Message(cursor.getString(cursor.getColumnIndex(MessageContract.Message._ID)), db);
            arr.add(temp);
        }

        cursor.close();
        return arr;
    }

}
