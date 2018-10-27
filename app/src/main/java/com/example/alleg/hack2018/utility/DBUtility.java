package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBUtility {

   static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


    public static long insertToDb(SQLiteDatabase db, String table, String nullColumnHack, ContentValues content) {
        long newId = db.insert(table, nullColumnHack, content);

        // now for firebase
        DatabaseReference tableRef = myRef.child(table);

        switch (table) {
            case "Users":
                // now to insert this value to the database
                tableRef.child(String.valueOf(newId)).setValue(new User(newId, content));
                break;
        }

        return newId;
    }

    // TODO
    public static void syncDatabase(SQLiteDatabase db) {}

    public static void login(String phone, String password){

        return;
    }
}
