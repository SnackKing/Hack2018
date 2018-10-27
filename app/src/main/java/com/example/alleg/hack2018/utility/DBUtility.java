package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.alleg.hack2018.MessagesActivity;
import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.models.Inventory;
import com.example.alleg.hack2018.models.Item;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.nio.ByteBuffer;

public class DBUtility extends AppCompatActivity {

    static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public static final String USER_KEY = "currUser";

    public static long insertToDb(SQLiteDatabase db, String table, String nullColumnHack, ContentValues content) {
        long newId = db.insert(table, nullColumnHack, content);

        // now for firebase
        DatabaseReference tableRef = myRef.child(table);

        switch (table) {
            case UserContract.User.TABLE_NAME:
                // now to insert this value to the database

                tableRef.child(String.valueOf(newId)).setValue(new User(newId, content));
                break;
            case MessageContract.Message.TABLE_NAME:

                tableRef.child(String.valueOf(newId)).setValue(new Message(newId, content));

                break;
            case InventoryContract.Inventory.TABLE_NAME:

                tableRef.child(String.valueOf(newId)).setValue(new Inventory(newId, content));

                break;
            case ItemContract.Item.TABLE_NAME:

                tableRef.child(String.valueOf(newId)).setValue(new Item(newId, content));

                break;
        }

        return newId;
    }

    // TODO
    public static void syncDatabase(SQLiteDatabase db) {}

    public void login(String phone, String password){

        // attach user to prefs
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson("Object goes here"); // myObject - instance of MyObject
        prefsEditor.putString(DBUtility.USER_KEY, json);
        prefsEditor.commit();

        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
    }

    public static byte[] toByteArray(int value) {
        return  ByteBuffer.allocate(4).putInt(value).array();
    }

    public static int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
