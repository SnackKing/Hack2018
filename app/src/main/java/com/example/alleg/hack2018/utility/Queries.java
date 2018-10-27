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
            User._ID + " INTEGER PRIMARY KEY," +
            User.COLUMN_NAME_NAME + " TEXT," +
            User.COLUMN_NAME_PHONE_NUMBER + " INTEGER," +
            User.COLUMN_NAME_PASSWORD + " BLOB," +
            User.COLUMN_NAME_SALT + " BLOB," +
            User.COLUMN_NAME_RESIDENT + " INTEGER);";

    static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + User.TABLE_NAME + ";";

    static final String SQL_CREATE_MESSAGE =
            "CREATE TABLE " + Message.TABLE_NAME + " (" +
            Message._ID + " INTEGER PRIMARY KEY," +
            Message.COLUMN_NAME_USER_ID + " INTEGER," +
            Message.COLUMN_NAME_MESSAGE + " TEXT," +
            Message.COLUMN_NAME_DESTINATION + " TEXT);";

    static final String SQL_DELETE_MESSAGE =
            "DROP TABLE IF EXISTS " + Message.TABLE_NAME + ";";

    static final String SQL_CREATE_ITEM =
            "CREATE TABLE " + Item.TABLE_NAME + " (" +
            Item._ID + " INTEGER PRIMARY KEY," +
            Item.COLUMN_NAME_NAME + " TEXT," +
            Item.COLUMN_NAME_PERISHABLE + " INTEGER," +
            Item.COLUMN_NAME_IMPORTANCE + " INTEGER);";

    static final String SQL_DELETE_ITEM =
            "DROP TABLE IF EXISTS " + Item.TABLE_NAME + ";";

    static final String SQL_CREATE_INVENTORY =
            "CREATE TABLE " + Inventory.TABLE_NAME + " (" +
            Inventory._ID + " INTEGER PRIMARY KEY," +
            Inventory.COLUMN_NAME_USER_ID + " INTEGER," +
            Inventory.COLUMN_NAME_ITEM + " TEXT," +
            Inventory.COLUMN_NAME_COUNT + " INTEGER);";

    static final String SQL_DELETE_INVENTORY =
            "DROP TABLE IF EXISTS " + Inventory.TABLE_NAME + ";";
}
