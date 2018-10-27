package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class User {
    private String name;
    private int phoneNumber;
    private byte[] salt;
    private byte[] password;
    private boolean resident;
    private long id;

    // from content values
    public User(long id, ContentValues values) {
        this.id = id;

        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case "phone_number":
                    this.phoneNumber = (int) value;
                    break;
                case "salt":
                    this.salt = (byte[]) value;
                    break;
                case "name":
                    this.name = value.toString();
                    break;
                case "password":
                    this.password = (byte[]) value;
                    break;
                case "resident":
                    this.resident = (int) value == 1;
                    break;
            }
        }
    }
}
