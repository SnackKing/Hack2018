package com.example.alleg.hack2018.models;

import android.provider.BaseColumns;

public final class UserContract {

    // prevent init
    private UserContract() { }

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "Users";
        public static final String COLUMN_NAME_PASSWORD = "Password";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_RESIDENT = "Resident";
    }

}
