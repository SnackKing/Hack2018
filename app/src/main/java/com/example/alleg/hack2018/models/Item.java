package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import com.example.alleg.hack2018.contracts.ItemContract;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Item {

    private String name;
    private boolean perishable;
    private long importance;

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
                    this.perishable = (boolean) value;
                    break;
                case ItemContract.Item.COLUMN_NAME_IMPORTANCE:
                    this.importance = (int) value;
                    break;
            }
        }
    }

}
