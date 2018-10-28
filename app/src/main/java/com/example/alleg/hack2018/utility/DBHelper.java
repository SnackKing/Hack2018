package com.example.alleg.hack2018.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 43;
    private static final String DATABASE_NAME = "Disaster.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Queries.SQL_CREATE_USER);
        db.execSQL(Queries.SQL_CREATE_INVENTORY);
        db.execSQL(Queries.SQL_CREATE_ITEM);
        db.execSQL(Queries.SQL_CREATE_MESSAGE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Queries.SQL_DELETE_USER);
        db.execSQL(Queries.SQL_DELETE_INVENTORY);
        db.execSQL(Queries.SQL_DELETE_ITEM);
        db.execSQL(Queries.SQL_DELETE_MESSAGE);
        onCreate(db);
    }
}