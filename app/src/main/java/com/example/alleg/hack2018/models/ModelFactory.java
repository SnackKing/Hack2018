package com.example.alleg.hack2018.models;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;

public class ModelFactory {
    public static final String INVENTORY_DESCRIPTOR = InventoryContract.TABLE_NAME;
    public static final String ITEM_DESCRIPTOR = ItemContract.TABLE_NAME;
    public static final String MESSAGE_DESCRIPTOR = MessageContract.TABLE_NAME;
    public static final String USER_DESCRIPTOR = UserContract.TABLE_NAME;

    public static DatabaseModel getModel(ContentValues cv) {
        DatabaseModel toReturn;

        if (cv.containsKey(InventoryContract.COLUMN_NAME_ITEM)) {
            toReturn = new Inventory(cv);
        } else if (cv.containsKey(MessageContract.COLUMN_NAME_MESSAGE)) {
            toReturn = new Message(cv);
        } else if (cv.containsKey(UserContract.COLUMN_NAME_PHONE_NUMBER)) {
            toReturn = new User(cv);
        } else {
            // item
            toReturn = new Item(cv);
        }

        return toReturn;
    }

    public static DatabaseModel getExistingModel(String id, SQLiteDatabase db, String type) {
        DatabaseModel toReturn = null;

        switch(type) {
            case USER_DESCRIPTOR:
                toReturn = new User(id, db);
                break;
            case MESSAGE_DESCRIPTOR:
                toReturn = new Message(id, db);
                break;
            case ITEM_DESCRIPTOR:
                toReturn = new Item(id, db);
                break;
            case INVENTORY_DESCRIPTOR:
                toReturn = new Inventory(id, db);
                break;
        }

        return toReturn;
    }
}
