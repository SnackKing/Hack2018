package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.utility.DBUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class User implements Serializable {
    public String name;
    public int phoneNumber;
    public int salt;
    public int password;
    public boolean resident;
    public String id;

    public User(String id, String name, int phone, byte[] salt, byte[] password, int resident) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phone;
        this.salt = DBUtility.fromByteArray(salt);
        this.password = DBUtility.fromByteArray(password);
        this.resident = resident == 1;
    }

    // from content values
    public User(ContentValues values) {
        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case UserContract.User.COLUMN_NAME_PHONE_NUMBER:
                    this.phoneNumber = Integer.parseInt(value.toString());
                    break;
                case UserContract.User.COLUMN_NAME_SALT:
                    this.salt = DBUtility.fromByteArray((byte[]) value);
                    break;
                case UserContract.User.COLUMN_NAME_NAME:
                    this.name = value.toString();
                    break;
                case UserContract.User.COLUMN_NAME_PASSWORD:
                    this.password = DBUtility.fromByteArray((byte[]) value);
                    break;
                case UserContract.User._ID:
                    this.id = value.toString();
                    break;
                case UserContract.User.COLUMN_NAME_RESIDENT:
                    this.resident = (int) value == 1;
                    break;
            }
        }
    }

    ArrayList<Message> getPrivateMessages() {
        ArrayList<Message> arr = new ArrayList<>();

        //TODO

        return arr;
    }
}
