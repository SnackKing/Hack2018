package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

public class InventoryContract implements BaseColumns{

    //prevents initialization
    private InventoryContract() { }

    public static final String TABLE_NAME = "Inventory";
    public static final String COLUMN_NAME_USER_ID = "User" + UserContract._ID;
    public static final String COLUMN_NAME_ITEM = "Item" + ItemContract._ID;
    public static final String COLUMN_NAME_COUNT = "Count";

    public static String[] getColumns() {
        return new String[] { COLUMN_NAME_USER_ID, COLUMN_NAME_ITEM, COLUMN_NAME_COUNT} ;
    }
}
