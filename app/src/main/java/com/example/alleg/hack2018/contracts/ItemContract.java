package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

import com.example.alleg.hack2018.utility.Queries;
import com.example.alleg.hack2018.utility.TableField;

public final class ItemContract implements BaseColumns {

    //prevent initialization
    private ItemContract() { }

    public static final String TABLE_NAME = "Items";
    public static final String COLUMN_NAME_NAME = "Name";
    public static final String COLUMN_NAME_PERISHABLE = "Perishable";
    public static final String COLUMN_NAME_IMPORTANCE = "Importance";

    public static TableField[] getTableFields() {
        return new TableField[] {
                new TableField(COLUMN_NAME_NAME, Queries.STRING, new String[] {"UNIQUE"}),
                new TableField(COLUMN_NAME_PERISHABLE, Queries.INT),
                new TableField(COLUMN_NAME_IMPORTANCE, Queries.INT)
        };
    }
}
