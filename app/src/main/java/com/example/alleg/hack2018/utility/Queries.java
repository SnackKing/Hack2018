package com.example.alleg.hack2018.utility;

import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.InventoryContract;

class Queries {

    // private utility class
    private Queries() {}

    static final String SQL_CREATE_USER =
            "CREATE TABLE " + UserContract.TABLE_NAME + " (" +
            UserContract._ID + " TEXT PRIMARY KEY," +
            UserContract.COLUMN_NAME_NAME + " TEXT," +
            UserContract.COLUMN_NAME_PHONE_NUMBER + " TEXT NOT NULL UNIQUE," +
            UserContract.COLUMN_NAME_PASSWORD + " BLOB," +
            UserContract.COLUMN_NAME_SALT + " BLOB," +
            UserContract.COLUMN_NAME_RESIDENT + " INT);";

    static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + UserContract.TABLE_NAME + ";";

    static final String SQL_CREATE_MESSAGE =
            "CREATE TABLE " + MessageContract.TABLE_NAME + " (" +
            MessageContract._ID + " TEXT PRIMARY KEY," +
            MessageContract.COLUMN_NAME_USER_ID + " TEXT," +
            MessageContract.COLUMN_NAME_MESSAGE + " TEXT," +
            MessageContract.COLUMN_NAME_DESTINATION_ID + " TEXT, " +
            MessageContract.COLUMN_NAME_TIME +" INT);";

    static final String SQL_DELETE_MESSAGE =
            "DROP TABLE IF EXISTS " + MessageContract.TABLE_NAME + ";";

    static final String SQL_CREATE_ITEM =
            "CREATE TABLE " + ItemContract.TABLE_NAME + " (" +
            ItemContract._ID + " TEXT PRIMARY KEY," +
            ItemContract.COLUMN_NAME_NAME + " TEXT UNIQUE," +
            ItemContract.COLUMN_NAME_PERISHABLE + " INT," +
            ItemContract.COLUMN_NAME_IMPORTANCE + " INT);";

    static final String SQL_DELETE_ITEM =
            "DROP TABLE IF EXISTS " + ItemContract.TABLE_NAME + ";";

    static final String SQL_CREATE_INVENTORY =
            "CREATE TABLE " + InventoryContract.TABLE_NAME + " (" +
            InventoryContract._ID + " TEXT PRIMARY KEY," +
            InventoryContract.COLUMN_NAME_USER_ID + " INT," +
            InventoryContract.COLUMN_NAME_ITEM + " TEXT," +
            InventoryContract.COLUMN_NAME_COUNT + " INT);";

    static final String SQL_DELETE_INVENTORY =
            "DROP TABLE IF EXISTS " + InventoryContract.TABLE_NAME + ";";
}
