package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

import com.example.alleg.hack2018.utility.Queries;
import com.example.alleg.hack2018.utility.TableField;

public final class UserContract implements BaseColumns {

    // prevent init
    private UserContract() { }

    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_NAME_PHONE_NUMBER = "Phone_Number";
    public static final String COLUMN_NAME_PASSWORD = "Password";
    public static final String COLUMN_NAME_SALT = "Salt";
    public static final String COLUMN_NAME_NAME = "Name";
    public static final String COLUMN_NAME_RESIDENT = "Resident";

    public static TableField[] getTableFields() {
        return new TableField[] {
                new TableField(COLUMN_NAME_NAME, Queries.STRING),
                new TableField(COLUMN_NAME_PHONE_NUMBER, Queries.STRING, new String[] {"NOT NULL", "UNIQUE"}),
                new TableField(COLUMN_NAME_PASSWORD, Queries.BYTE_ARR),
                new TableField(COLUMN_NAME_SALT, Queries.BYTE_ARR),
                new TableField(COLUMN_NAME_RESIDENT, Queries.BOOL)
        };
    }
}
