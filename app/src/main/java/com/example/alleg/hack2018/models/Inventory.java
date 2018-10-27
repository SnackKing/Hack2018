package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import com.example.alleg.hack2018.contracts.InventoryContract;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Inventory implements Serializable {

    public long id;
    public long userId;
    public String item;
    public int count;

    public Inventory(long id, ContentValues values){
        this.id = id;

        Set<Map.Entry<String, Object>> s=values.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            switch (key) {
                case InventoryContract.Inventory.COLUMN_NAME_ITEM:
                    this.item = value.toString();
                    break;
                case InventoryContract.Inventory.COLUMN_NAME_COUNT:
                    this.count = (int) value;
                    break;
                case InventoryContract.Inventory.COLUMN_NAME_USER_ID:
                    this.userId = (long) value;
            }
        }
    }

}
