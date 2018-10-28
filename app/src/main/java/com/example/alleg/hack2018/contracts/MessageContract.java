package com.example.alleg.hack2018.contracts;

import android.provider.BaseColumns;

public final class MessageContract {

    //prevent initialization
    private MessageContract() { }

    public static class Message implements BaseColumns {
        public static final String TABLE_NAME = "Messages";
        public static final String COLUMN_NAME_USER_ID = "User" + UserContract.User._ID;
        public static final String COLUMN_NAME_MESSAGE = "Message";
        public static final String COLUMN_NAME_DESTINATION_ID = "Destination" + UserContract.User._ID;
        public static final String COLUMN_NAME_TIME = "Time";
    }
}
