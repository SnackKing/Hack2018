package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

import com.example.alleg.hack2018.utility.Queries;
import com.example.alleg.hack2018.utility.TableField;

public final class MessageContract implements BaseColumns {

    //prevent initialization
    private MessageContract() { }

    public static final String TABLE_NAME = "Messages";
    public static final String COLUMN_NAME_USER_ID = "User" + UserContract._ID;
    public static final String COLUMN_NAME_MESSAGE = "Message";
    public static final String COLUMN_NAME_DESTINATION_ID = "Destination" + UserContract._ID;
    public static final String COLUMN_NAME_TIME = "Time";

    public static TableField[] getTableFields() {
        return new TableField[] {
                new TableField(COLUMN_NAME_USER_ID, Queries.STRING),
                new TableField(COLUMN_NAME_DESTINATION_ID, Queries.STRING),
                new TableField(COLUMN_NAME_MESSAGE, Queries.STRING),
                new TableField(COLUMN_NAME_TIME, Queries.INT)
        };
    }
}
