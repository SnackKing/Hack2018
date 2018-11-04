package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.bridgefy.sdk.client.Bridgefy;
import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.models.DatabaseModel;
import com.example.alleg.hack2018.models.Inventory;
import com.example.alleg.hack2018.models.Item;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.ModelFactory;
import com.example.alleg.hack2018.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class DBUtility extends AppCompatActivity {
    static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public static final String USER_KEY = "currUser";

    // how long the thread syncs
    public static final int MS_WAIT_THREAD_CHECK = 8000;

    public static final String PUBLIC_MESSAGE_DEST = "-1";

    public static final int SYNC_THREAD_LIMIT = 1;

    // pseudo singleton
    private static int syncThreads = 0;

    private static final String MSG_CONTEXT_ACCESSOR = "text";

    private Context context;
    private SyncThread sync;
    private SQLiteDatabase db;
    private SQLiteDatabase dbr;

    public DBUtility(Context con) {
        this.context = con;
        DBHelper temp = new DBHelper(con);
        this.db = temp.getWritableDatabase();
        this.dbr = temp.getReadableDatabase();

        if (DBUtility.syncThreads < SYNC_THREAD_LIMIT) {
            this.sync = new SyncThread(this, db, dbr);
            this.sync.start();
            DBUtility.syncThreads += 1;
        }
    }

    public HashMap dataToHashmap(){
        HashMap<String, HashMap<String, DatabaseModel>> hmap = new HashMap<>();

        String[] tables = new String[] {UserContract.TABLE_NAME, MessageContract.TABLE_NAME, ItemContract.TABLE_NAME, InventoryContract.TABLE_NAME };

        for (String table : tables) {
            HashMap<String, DatabaseModel> modelMap = new HashMap<>();

            String selectQuery = "SELECT * FROM " + table;
            Cursor cursor = this.dbr.rawQuery(selectQuery,new String[]{});

            while(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                DatabaseModel toAdd = ModelFactory.getExistingModel(id, dbr, table);

                modelMap.put(id, toAdd);
            }

            hmap.put(table, modelMap);
        }

        HashMap<String, String> newHash = new HashMap<>();

        String json = "";// TODO

        newHash.put(MSG_CONTEXT_ACCESSOR, json);

        return newHash;
    }
    
    public void insertToDb(String table, String nullColumnHack, ContentValues content) {

        try {
            Bridgefy.sendBroadcastMessage(this.dataToHashmap());
        } catch (IllegalStateException e) {
            // do nothing
            // jank but i've been awake for 30 hours
        }

        db.insert(table, nullColumnHack, content);

        DatabaseModel mod = ModelFactory.getModel(content);

        if (this.isConnected()) {
            DBUtility.addToFirebase(table, mod);
        }
    }

    public int login(SQLiteDatabase db, String phone, String password){
        String selectQuery = "SELECT * FROM " + UserContract.TABLE_NAME + " WHERE "
                + UserContract.COLUMN_NAME_PHONE_NUMBER + " = " + phone;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        if (cursor.getCount() == 0) {
            //Incorrect phone number
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        byte[] salt = cursor.getBlob(cursor.getColumnIndex(UserContract.COLUMN_NAME_SALT));
        byte[] saltedPsd = cursor.getBlob(cursor.getColumnIndex(UserContract.COLUMN_NAME_PASSWORD));

        if (!Passwords.isExpectedPassword(password.toCharArray(), salt, saltedPsd)) {
            //Incorrect password
            cursor.close();
            return -2;
        }

        User x = (User) ModelFactory.getExistingModel(cursor.getString(cursor.getColumnIndex(UserContract._ID)), db, UserContract.TABLE_NAME);

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
    public HashSet<String> getIDSet(String tableName) {
        HashSet<String> toReturn = new HashSet<>();

        String colName = BaseColumns._ID;

        String selectQuery = "SELECT * FROM " + tableName;
        Cursor cursor = dbr.rawQuery(selectQuery, new String[] {});

        while (cursor.moveToNext()) {
            toReturn.add(cursor.getString(cursor.getColumnIndex(colName)));
        }

        cursor.close();

        return toReturn;
    }

    public HashMap<String, HashSet<String>> getTableIdSets(HashMap<String, Map<String, Map<String, String>>> input) {
        HashMap<String, HashSet<String>> out = new HashMap<>();

        // for all keys in  each table, return tablename to set of keys
        for (String t : input.keySet()) {
            // this is table
            HashSet<String> keys = new HashSet<>();

            keys.addAll(input.get(t).keySet());

            out.put(t, keys);
        }

        return out;
    }

    public HashMap<String, HashSet<String>> getIdsToAdd(HashMap<String, Map<String, Map<String, String>>> input) {

        HashMap<String, HashSet<String>> out = new HashMap<>();

        HashMap<String, HashSet<String>> in = getTableIdSets(input);

        for (String key : in.keySet()) {
            // key is table
            HashSet<String> existingKeys = this.getIDSet(key);

            HashSet<String> newKeys = in.get(key);

            newKeys.removeAll(existingKeys);

            out.put(key, newKeys);
        }

        return out;
    }

    public void updateLocal(HashMap<String, Map<String, Map<String, String>>> input) {
        HashMap<String, HashSet<String>> keysToAdd = getIdsToAdd(input);

        for (String table : keysToAdd.keySet()) {
            for (String id : keysToAdd.get(table)) {
                Map<String, String> dataToAdd = input.get(table).get(id);

                ContentValues values = new ContentValues();

                for (String label : dataToAdd.keySet()) {
                    if (label.equals(UserContract.COLUMN_NAME_SALT) || label.equals(UserContract.COLUMN_NAME_PASSWORD)) {
                        byte[] val = DBUtility.toByteArray( Integer.valueOf(dataToAdd.get(label)));

                        values.put(label, val);
                    } else if (label.equals(UserContract.COLUMN_NAME_RESIDENT) || label.equals(MessageContract.COLUMN_NAME_TIME)) {
                        values.put(label, Integer.valueOf(dataToAdd.get(label)));
                    } else {
                        values.put(label, dataToAdd.get(label));
                    }
                }

                this.insertToDb(table, null, values);
            }
        }
    }

    public static Set<String> getIDSetCloud(String tableName) {
        Set<String> toReturn = new HashSet<>();

        CountDownLatch done = new CountDownLatch(1);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.child(tableName).getChildren();
                
                for (DataSnapshot child : children) {
                    toReturn.add(child.getKey());
                }

                done.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    public static DatabaseModel getRecordFromFirebase(String table, String id) {
        CountDownLatch done = new CountDownLatch(1);

        ArrayList<DatabaseModel> hack = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                switch(table) {
                    case UserContract.TABLE_NAME:
                        hack.add(dataSnapshot.child(table).child(id).getValue(User.class));
                        break;
                    case ItemContract.TABLE_NAME:
                        hack.add(dataSnapshot.child(table).child(id).getValue(Item.class));
                        break;
                    case InventoryContract.TABLE_NAME:
                        hack.add(dataSnapshot.child(table).child(id).getValue(Inventory.class));
                        break;
                    case MessageContract.TABLE_NAME:
                        hack.add(dataSnapshot.child(table).child(id).getValue(Message.class));
                        break;
                }

                done.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return hack.get(0);
    }

    public static void addToFirebase(String tableName, DatabaseModel obj) {
        DatabaseReference tableRef = myRef.child(tableName);

        tableRef.child(obj.getID()).setValue(obj);
    }

    public static HashMap<String, HashMap<String, DatabaseModel>> getMessageContent(com.bridgefy.sdk.client.Message message) {
        HashMap<String, HashMap<String, DatabaseModel>> toReturn = new HashMap<>();

        String json = (String) message.getContent().get(MSG_CONTEXT_ACCESSOR);

        // TODO de-serialize

        return toReturn;
    }
}
