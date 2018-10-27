package com.example.alleg.hack2018;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DBUtility {

    public static void insertToDb(SQLiteDatabase db, String table, String nullColumnHack, ContentValues content) {
        db.insert(table, nullColumnHack, content);

        // TODO
        // insert to firebase
    }

    // TODO
    public static void syncDatabase(SQLiteDatabase db) {}
}
