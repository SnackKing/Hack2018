package com.example.alleg.hack2018.utility;

import com.example.alleg.hack2018.contracts.UserContract.User;
import com.example.alleg.hack2018.contracts.MessageContract.Message;
import com.example.alleg.hack2018.contracts.ItemContract.Item;
import com.example.alleg.hack2018.contracts.InventoryContract.Inventory;

class Queries {

    // private utility class
    private Queries() {}

    static final String SQL_CREATE_USER =
            "CREATE TABLE " + User.TABLE_NAME + " (" +
            User._ID + " TEXT PRIMARY KEY," +
            User.COLUMN_NAME_NAME + " TEXT," +
            User.COLUMN_NAME_PHONE_NUMBER + " INT NOT NULL UNIQUE," +
            User.COLUMN_NAME_PASSWORD + " BLOB," +
            User.COLUMN_NAME_SALT + " BLOB," +
            User.COLUMN_NAME_RESIDENT + " INT);";

    static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + User.TABLE_NAME + ";";

    static final String SQL_CREATE_MESSAGE =
            "CREATE TABLE " + Message.TABLE_NAME + " (" +
            Message._ID + " TEXT PRIMARY KEY," +
            Message.COLUMN_NAME_USER_ID + " TEXT," +
            Message.COLUMN_NAME_MESSAGE + " TEXT," +
            Message.COLUMN_NAME_DESTINATION_ID + " INT);";

    static final String SQL_DELETE_MESSAGE =
            "DROP TABLE IF EXISTS " + Message.TABLE_NAME + ";";

    static final String SQL_CREATE_ITEM =
            "CREATE TABLE " + Item.TABLE_NAME + " (" +
            Item._ID + " TEXT PRIMARY KEY," +
            Item.COLUMN_NAME_NAME + " TEXT UNIQUE," +
            Item.COLUMN_NAME_PERISHABLE + " INT," +
            Item.COLUMN_NAME_IMPORTANCE + " INT);";

    static final String SQL_DELETE_ITEM =
            "DROP TABLE IF EXISTS " + Item.TABLE_NAME + ";";

    static final String SQL_CREATE_INVENTORY =
            "CREATE TABLE " + Inventory.TABLE_NAME + " (" +
            Inventory._ID + " TEXT PRIMARY KEY," +
            Inventory.COLUMN_NAME_USER_ID + " INT," +
            Inventory.COLUMN_NAME_ITEM + " TEXT," +
            Inventory.COLUMN_NAME_COUNT + " INT);";

    static final String SQL_DELETE_INVENTORY =
            "DROP TABLE IF EXISTS " + Inventory.TABLE_NAME + ";";
}
