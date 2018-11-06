package com.example.alleg.hack2018.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.utility.DBUtility;
import com.example.alleg.hack2018.utility.Queries;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Message implements DatabaseModel {

    private String id; // id of this record

    /*
     * TODO Sahil
     *
     * Do this to-do last
     *
     * Once all others are completed, make these two id's private
     * this will prevent people from accessing in the id's directly, and will
     * encapsulate the id's
     *
     * Once these are private, there will be some errors. Go and find where these id's are accessed,
     * and then switch them to your newly implemented methods. this should make sense.
     *
     * For example, if (we will) want to make a "getFromDBOr404" method, we can call that in these
     * encapsulated methods, and the people using our calls will not need to know.
     */
    public String senderId; // id of sender
    public String recipId; // id of intended recipient

    public String msg; // message
    public int time; //time of message

    // for firebase
    public Message() {}

    // pull from db
    Message(String id, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + MessageContract.TABLE_NAME
                + " WHERE " + MessageContract._ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            //Can't find id
            //TODO
        }

        this.id = id;
        this.senderId = cursor.getString(cursor.getColumnIndex(MessageContract.COLUMN_NAME_USER_ID));
        this.recipId = cursor.getString(cursor.getColumnIndex(MessageContract.COLUMN_NAME_DESTINATION_ID));
        this.msg = cursor.getString(cursor.getColumnIndex(MessageContract.COLUMN_NAME_MESSAGE));
        this.time = cursor.getInt(cursor.getColumnIndex(MessageContract.COLUMN_NAME_TIME));
        cursor.close();
    }

    Message(ContentValues values){
        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case MessageContract.COLUMN_NAME_MESSAGE:
                    this.msg = value.toString();
                    break;
                case MessageContract.COLUMN_NAME_DESTINATION_ID:
                    this.recipId = value.toString();
                    break;
                case MessageContract.COLUMN_NAME_USER_ID:
                    this.senderId = value.toString();
                    break;
                case MessageContract._ID:
                    this.id = value.toString();
                    break;
                case MessageContract.COLUMN_NAME_TIME:
                    this.time = (int) value;
                    break;
            }
        }
    }

    // this is like an attribute
    public User sender() {
        // TODO sahil
        // return the sender of this message, but as a user object, not an id
        // this is a one liner
        return null;
    }

    // this is like an attribute
    public User receiver() {
        // TODO sahil
        // return the sender of this message, but as a user object, not an id
        // this is a one liner
        return null;
    }

    public static ArrayList<Message> getPublicMessages(SQLiteDatabase db) {
        ArrayList<Message> arr = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + MessageContract.TABLE_NAME
                + " WHERE " + MessageContract.COLUMN_NAME_DESTINATION_ID + " = \"" + DBUtility.PUBLIC_MESSAGE_DEST + "\"";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});

        while(cursor.moveToNext()) {
            Message temp = (Message) ModelFactory.getExistingModel(cursor.getString(cursor.getColumnIndex(MessageContract._ID)), db, MessageContract.TABLE_NAME);
            arr.add(temp);
        }

        cursor.close();
        return arr;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(MessageContract._ID, this.id);
        values.put(MessageContract.COLUMN_NAME_TIME, this.time);
        values.put(MessageContract.COLUMN_NAME_MESSAGE, this.msg);
        values.put(MessageContract.COLUMN_NAME_USER_ID, this.senderId);
        values.put(MessageContract.COLUMN_NAME_DESTINATION_ID, this.recipId);
        values.put(MessageContract.COLUMN_NAME_TIME, this.time);

        return values;
    }

    @Exclude
    public String getID() {
        return this.id;
    }

    @Exclude
    public String getTableName() {
        return MessageContract.TABLE_NAME;
    }

    @Exclude
    public String getCreateTable() {
        return Queries.getCreateTableStatement(this.getTableName(), MessageContract.getTableFields());
    }

    @Exclude
    @Override
    public String getDropTable() {
        return DatabaseModel.super.getDropTable();
    }
}
