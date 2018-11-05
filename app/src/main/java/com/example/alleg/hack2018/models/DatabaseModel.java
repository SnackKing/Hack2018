package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;

import java.io.Serializable;

public interface DatabaseModel extends Serializable {
    ContentValues getContentValues();
    String getID();

    static java.lang.Class getClass(String tbl) {
        // will be overwritten
        java.lang.Class cls = DatabaseModel.class;

        switch (tbl) {
            case InventoryContract.TABLE_NAME:
                cls = Inventory.class;
                break;
            case ItemContract.TABLE_NAME:
                cls = Item.class;
                break;
            case MessageContract.TABLE_NAME:
                cls = Message.class;
                break;
            case UserContract.TABLE_NAME:
                cls = User.class;
                break;
        }

        return cls;
    }
}
