package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBUtility {

   static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


    public static long insertToDb(SQLiteDatabase db, String table, String nullColumnHack, ContentValues content) {
        long newId = db.insert(table, nullColumnHack, content);

        // now for firebase
        DatabaseReference tableRef = myRef.child(table);

        // now to insert this value to the database
        tableRef.child(String.valueOf(newId)).setValue(content);

        return newId;
    }

    // TODO
    public static void syncDatabase(SQLiteDatabase db) {}
}
