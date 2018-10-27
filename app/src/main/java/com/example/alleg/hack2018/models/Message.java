package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import com.example.alleg.hack2018.contracts.MessageContract;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Message {
    private long senderid; // id of sender
    private long recipid; // id of intended recipient
    private String msg; // message

    public Message(long id, ContentValues values){
        this.senderid = id;

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
                    this.recipid = (int) value;
                    break;
            }
        }


    }

}
