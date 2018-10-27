package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.example.alleg.hack2018.MessagesActivity;
import com.example.alleg.hack2018.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class DBUtility extends AppCompatActivity {

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

    public void login(String phone, String password){

        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson("Object goes here"); // myObject - instance of MyObject
        prefsEditor.putString("Key", json);
        prefsEditor.commit();

        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
    }
}
