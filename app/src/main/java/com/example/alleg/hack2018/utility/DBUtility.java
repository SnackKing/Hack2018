package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

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
import java.util.HashSet;
import java.util.Set;

public class DBUtility extends AppCompatActivity {

    static ArrayList<User> notSentUsers = new ArrayList<>();
    static ArrayList<Message> notSentMessages = new ArrayList<>();
    static ArrayList<Item> notSentItems = new ArrayList<>();
    static ArrayList<Inventory> notSentInventories = new ArrayList<>();

    static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public static final String USER_KEY = "currUser";

    private Context context;
    private SyncThread sync;
    private SQLiteDatabase db;

    public DBUtility(Context con) {
        this.context = con;
        this.db = new DBHelper(this.context).getWritableDatabase();
        this.sync = new SyncThread(this, db);
    }

    public void insertToDb(String table, String key, String nullColumnHack, ContentValues content) {

        db.insert(table, nullColumnHack, content);

        // now for firebase
        DatabaseReference tableRef = myRef.child(table);

        switch (table) {
            case UserContract.User.TABLE_NAME:
                // now to insert this value to the database

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(key)).setValue(new User(content));
                } else {
                    notSentUsers.add(new User(content));
                    sync.start();
                }

                break;
            case MessageContract.Message.TABLE_NAME:

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(key)).setValue(new Message(content));
                } else {
                    notSentMessages.add(new Message(content));
                    sync.start();
                }

                break;
            case InventoryContract.Inventory.TABLE_NAME:

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(key)).setValue(new Inventory(content));
                } else {
                    notSentInventories.add(new Inventory(content));
                    sync.start();
                }

                break;
            case ItemContract.Item.TABLE_NAME:

                if (this.isConnected()) {
                    tableRef.child(String.valueOf(key)).setValue(new Item(content));
                } else {
                    notSentItems.add(new Item(content));
                    sync.start();
                }

                break;
        }
    }

    public int login(SQLiteDatabase db, String phone, String password){
        String selectQuery = "SELECT * FROM " + UserContract.User.TABLE_NAME + " WHERE "
                + UserContract.User.COLUMN_NAME_PHONE_NUMBER + " = " + phone;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        if (cursor.getCount() == 0) {
            //Incorrect phone number
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        byte[] salt = cursor.getBlob(cursor.getColumnIndex("Salt"));
        byte[] saltedPsd =
                cursor.getBlob(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_PASSWORD));

        if (!Passwords.isExpectedPassword(password.toCharArray(), salt, saltedPsd)) {
            //Incorrect password
            cursor.close();
            return -2;
        }

        User x = new User(cursor.getString(cursor.getColumnIndex(UserContract.User._ID)),
                cursor.getString(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_NAME)),
                cursor.getString(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_PHONE_NUMBER)),
                salt, saltedPsd, cursor.getInt(cursor.getColumnIndex(UserContract.User.COLUMN_NAME_RESIDENT)));

        // attach user to prefs
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(x); // myObject - instance of MyObject
        prefsEditor.putString(DBUtility.USER_KEY, json);
        prefsEditor.apply();
        prefsEditor.commit();

        cursor.close();
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

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    // return the full set of all UUID's present in tableName
    public Set<String> getIDSet(String tableName) {
        Set<String> toReturn = new HashSet<>();

        String colName = null;

        switch(tableName) {
            case InventoryContract.Inventory.TABLE_NAME:
                colName = InventoryContract.Inventory._ID;
                break;
            case UserContract.User.TABLE_NAME:
                colName = UserContract.User._ID;
                break;
            case ItemContract.Item.TABLE_NAME:
                colName = ItemContract.Item._ID;
                break;
            case MessageContract.Message.TABLE_NAME:
                colName = MessageContract.Message._ID;
                break;
        }

        DBHelper help = new DBHelper(this.context);
        SQLiteDatabase db = help.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});

        for (int i = 0; i < cursor.getCount(); i++) {
            toReturn.add(cursor.getString(cursor.getColumnIndex(colName)));
        }

        cursor.close();
        help.close();
        return toReturn;
    }

    public static Set<String> getIDSetCloud(String tableName) {
        Set<String> toReturn = new HashSet<>();

        // TODO

        return toReturn;
    }
}
