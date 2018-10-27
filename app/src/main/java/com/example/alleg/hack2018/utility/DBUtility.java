package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.ArrayList;

public class DBUtility extends AppCompatActivity {

    static ArrayList<User> notSentUsers = new ArrayList<>();
    static ArrayList<Message> notSentMessages = new ArrayList<>();
    static ArrayList<Item> notSentItems = new ArrayList<>();
    static ArrayList<Inventory> notSentInventories = new ArrayList<>();

    static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public static final String USER_KEY = "currUser";

    public static long insertToDb(SQLiteDatabase db, String table, String nullColumnHack, ContentValues content) {
        DBUtility util = new DBUtility();

        long newId = db.insert(table, nullColumnHack, content);

        // now for firebase
        DatabaseReference tableRef = myRef.child(table);

        switch (table) {
            case UserContract.User.TABLE_NAME:
                // now to insert this value to the database

                if (util.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new User(newId, content));
                } else {
                    notSentUsers.add(new User(newId, content));
                }

                break;
            case MessageContract.Message.TABLE_NAME:

                if (util.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new Message(newId, content));
                } else {
                    notSentMessages.add(new Message(newId, content));
                }

                break;
            case InventoryContract.Inventory.TABLE_NAME:

                if (util.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new Inventory(newId, content));
                } else {
                    notSentInventories.add(new Inventory(newId, content));
                }

                break;
            case ItemContract.Item.TABLE_NAME:

                if (util.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new Item(newId, content));
                } else {
                    notSentItems.add(new Item(newId, content));
                }

                break;
        }

        return newId;
    }

    // TODO : save changes and deletions
    public static void syncDatabase(SQLiteDatabase db) {
        // empty each of the not sent arrays

        while (notSentMessages.size() > 0 ) {
            Message msg = notSentMessages.remove(notSentMessages.size() - 1);

            myRef.child(MessageContract.Message.TABLE_NAME).child(String.valueOf(msg.id)).setValue(msg);
        }

        while (notSentUsers.size() > 0) {
            User usr = notSentUsers.remove(notSentUsers.size() - 1);

            myRef.child(UserContract.User.TABLE_NAME).child(String.valueOf(usr.id)).setValue(usr);
        }

        while (notSentInventories.size() > 0) {
            Inventory i = notSentInventories.remove(notSentInventories.size() - 1);

            myRef.child(InventoryContract.Inventory.TABLE_NAME).child(String.valueOf(i.id)).setValue(i);
        }

        while (notSentItems.size() > 0) {
            Item i = notSentItems.remove(notSentItems.size() - 1);

            myRef.child(ItemContract.Item.TABLE_NAME).child(String.valueOf(i.id)).setValue(i);
        }
    }

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

    public boolean isConnected() {
        Context context = getApplicationContext();

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
