package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    private Context context;

    public DBUtility(Context con) {
        this.context = con;
    }

    public long insertToDb(SQLiteDatabase db, String table, String nullColumnHack, ContentValues content) {
        long newId = db.insert(table, nullColumnHack, content);

        // now for firebase
        DatabaseReference tableRef = myRef.child(table);

        switch (table) {
            case UserContract.User.TABLE_NAME:
                // now to insert this value to the database

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new User(newId, content));
                } else {
                    notSentUsers.add(new User(newId, content));
                }

                break;
            case MessageContract.Message.TABLE_NAME:

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new Message(newId, content));
                } else {
                    notSentMessages.add(new Message(newId, content));
                }

                break;
            case InventoryContract.Inventory.TABLE_NAME:

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new Inventory(newId, content));
                } else {
                    notSentInventories.add(new Inventory(newId, content));
                }

                break;
            case ItemContract.Item.TABLE_NAME:

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(newId)).setValue(new Item(newId, content));
                } else {
                    notSentItems.add(new Item(newId, content));
                }

                break;
        }

        return newId;
    }

    // TODO : save changes and deletions
    public void syncDatabase(SQLiteDatabase db) {
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

    public int login(SQLiteDatabase db, String phone, String password){
        String selectQuery = "SELECT * FROM " + UserContract.User.TABLE_NAME + " WHERE "
                + UserContract.User.COLUMN_NAME_PHONE_NUMBER + " = " + phone;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        if (cursor.getCount() == 0) {
            //Incorrect phone number
            return -1;
        }

        String out = "";
        for (String val : cursor.getColumnNames()) {
            out += val + " ";
        }
        
        Log.d("COLUMN NAMES", out);
        byte[] salt = cursor.getBlob(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_SALT));
        byte[] saltedPsd =
                cursor.getBlob(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_PASSWORD));

        if (!Passwords.isExpectedPassword(password.toCharArray(), salt, saltedPsd)) {
            //Incorrect password
            return -2;
        }

        User x = new User(cursor.getLong(cursor.getColumnIndex(UserContract.User._ID)),
                cursor.getString(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_NAME)),
                cursor.getInt(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_PHONE_NUMBER)),
                salt, saltedPsd, cursor.getInt(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_RESIDENT)));

        // attach user to prefs
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(x); // myObject - instance of MyObject
        prefsEditor.putString(DBUtility.USER_KEY, json);
        prefsEditor.commit();

        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
        return 1;
    }

    public static byte[] toByteArray(int value) {
        return  ByteBuffer.allocate(4).putInt(value).array();
    }

    public static int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
