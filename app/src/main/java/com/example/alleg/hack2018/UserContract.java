package com.example.alleg.hack2018;

import android.provider.BaseColumns;

public final class UserContract {

    // prevent init
    private UserContract() { }

    public static class User implements BaseColumns {
        //public static final String TABLE_NAME = "entry";
        //public static final String COLUMN_NAME_TITLE = "title";
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PASSWORD = "password";

    }

}
