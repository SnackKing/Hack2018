package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBUtility {

   static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


    public static void insertToDb(SQLiteDatabase db, String table, String nullColumnHack, ContentValues content) {
        db.insert(table, nullColumnHack, content);

        // TODO
        // insert to firebase
        myRef.child(table).child("1").setValue("object");
    }

    // TODO
    public static void syncDatabase(SQLiteDatabase db) {}
}
