package com.example.alleg.hack2018;

import android.provider.BaseColumns;

public class InventoryContract {

    //prevents initialization
    private InventoryContract() { }

    public static class Inventory implements BaseColumns{
        public static final String TABLE_NAME = "Inventory";
        public static final String COLUMN_NAME_USER_ID = "User" + UserContract.User._ID;
        public static final String COLUMN_NAME_ITEM = "Item" + ItemContract.Item._ID;
        public static final String COLUMN_NAME_COUNT = "Count";
    }
}
