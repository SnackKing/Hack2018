package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import com.example.alleg.hack2018.contracts.ItemContract;

import java.io.SequenceInputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Item implements Serializable {

    public String id;
    public String name;
    public boolean perishable;
    public long importance;

    public Item(ContentValues values){
        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case ItemContract.Item.COLUMN_NAME_NAME:
                    this.name = value.toString();
                    break;
                case ItemContract.Item.COLUMN_NAME_PERISHABLE:
                    this.perishable = (int) value == 1;
                    break;
                case ItemContract.Item.COLUMN_NAME_IMPORTANCE:
                    this.importance = (int) value;
                    break;
                case ItemContract.Item._ID:
                    this.id = value.toString();
                    break;
            }
        }
    }

}
