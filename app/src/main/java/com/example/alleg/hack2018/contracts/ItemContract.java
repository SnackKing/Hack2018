package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

public final class ItemContract implements BaseColumns {

    //prevent initialization
    private ItemContract() { }

    public static final String TABLE_NAME = "Items";
    public static final String COLUMN_NAME_NAME = "Name";
    public static final String COLUMN_NAME_PERISHABLE = "Perishable";
    public static final String COLUMN_NAME_IMPORTANCE = "Importance";

    public static String[] getColumns() {
        return new String[] { COLUMN_NAME_NAME, COLUMN_NAME_PERISHABLE, COLUMN_NAME_IMPORTANCE };
    }

}
