package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

import com.example.alleg.hack2018.utility.Queries;
import com.example.alleg.hack2018.utility.TableField;

public class InventoryContract implements BaseColumns {

    //prevents initialization
    private InventoryContract() { }

    public static final String TABLE_NAME = "Inventory";
    public static final String COLUMN_NAME_USER_ID = "User" + UserContract._ID;
    public static final String COLUMN_NAME_ITEM = "Item" + ItemContract._ID;
    public static final String COLUMN_NAME_COUNT = "Count";

    public static TableField[] getTableFields() {
        return new TableField[] {
                new TableField(COLUMN_NAME_USER_ID, Queries.STRING),
                new TableField(COLUMN_NAME_ITEM, Queries.STRING),
                new TableField(COLUMN_NAME_COUNT, Queries.INT)
        };
    }
}
