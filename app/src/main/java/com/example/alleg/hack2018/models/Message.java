package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import com.example.alleg.hack2018.contracts.MessageContract;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Message implements Serializable {
    public String id; // id of this record
    public long senderId; // id of sender
    public long recipId; // id of intended recipient
    public String msg; // message

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
                    this.recipId = (long) value;
                    break;
                case MessageContract.Message.COLUMN_NAME_USER_ID:
                    this.senderId = (long) value;
                    break;
                case MessageContract.Message._ID:
                    this.id = value.toString();
                    break;
            }
        }


    }

}
