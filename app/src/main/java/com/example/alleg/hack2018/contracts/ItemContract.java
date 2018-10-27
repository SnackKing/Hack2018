package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

public final class ItemContract {

    //prevent initialization
    private ItemContract() { }

    public static class Item implements BaseColumns {
        public static final String TABLE_NAME = "Items";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_PERISHABLE = "Perishable";
        public static final String COLUMN_NAME_IMPORTANCE = "Importance";
    }
}
