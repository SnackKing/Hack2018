package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

public final class MessageContract implements BaseColumns {

    //prevent initialization
    private MessageContract() { }

    public static final String TABLE_NAME = "Messages";
    public static final String COLUMN_NAME_USER_ID = "User" + UserContract._ID;
    public static final String COLUMN_NAME_MESSAGE = "Message";
    public static final String COLUMN_NAME_DESTINATION_ID = "Destination" + UserContract._ID;
    public static final String COLUMN_NAME_TIME = "Time";

    public static String[] getColumns() {
        return new String[] { COLUMN_NAME_USER_ID, COLUMN_NAME_MESSAGE, COLUMN_NAME_DESTINATION_ID, COLUMN_NAME_TIME };
    }

}
