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
import com.example.alleg.hack2018.models.ModelFactory;
import com.example.alleg.hack2018.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class DBUtility extends AppCompatActivity {
    private static DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

    public static final String USER_KEY = "currUser";

    public static final String PUBLIC_MESSAGE_DEST = "-1";

    private static final int SYNC_THREAD_LIMIT = 1;

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

    public static byte[] toByteArray(int value) {
        return  ByteBuffer.allocate(4).putInt(value).array();
    }

    public static int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    static Set<String> getIDSetCloud(String tableName) {
        Set<String> toReturn = new HashSet<>();

        CountDownLatch done = new CountDownLatch(1);

        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

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

        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                java.lang.Class cls = DatabaseModel.getClass(table);

                hack.add((DatabaseModel) dataSnapshot.child(table).child(id).getValue(cls));

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
        DatabaseReference tableRef = firebaseRef.child(tableName);

        tableRef.child(obj.getID()).setValue(obj);
    }

    public static Map<String, Map<String, DatabaseModel>> getMessageContent(com.bridgefy.sdk.client.Message message) {
        Map<String, Map<String, DatabaseModel>> toReturn = new HashMap<>();
        String json = (String) message.getContent().get(MSG_CONTEXT_ACCESSOR);
        try {
            JSONObject tree = new JSONObject(json);

            for (Iterator<String> it = tree.keys(); it.hasNext(); ) {
                String key = it.next();

                // assemble the data for this table
                JSONObject objects = (JSONObject) tree.get(key);

                Map<String, DatabaseModel> inner = new HashMap<>();

                for (Iterator<String> it1 = objects.keys(); it1.hasNext(); ) {
                    String id = it1.next();

                    // will be overwritten
                    java.lang.Class cls = DatabaseModel.getClass(key);

                    Gson gson = new Gson();

                    inner.put(id, (DatabaseModel) gson.fromJson(objects.getJSONObject(id).toString(), cls));
                }

                toReturn.put(key, inner);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    // Non Static Methods are below

    // Private methods

    private Map<String, Set<String>> getTableIdSets(Map<String, Map<String, DatabaseModel>> input) {
        Map<String, Set<String>> out = new HashMap<>();

        // for all keys in  each table, return tablename to set of keys
        for (String t : input.keySet()) {
            // this is table
            HashSet<String> keys = new HashSet<>();

            keys.addAll(input.get(t).keySet());

            out.put(t, keys);
        }

        return out;
    }

    private Map<String, Set<String>> getIdsToAdd(Map<String, Map<String, DatabaseModel>> input) {

        Map<String, Set<String>> out = new HashMap<>();

        Map<String, Set<String>> in = getTableIdSets(input);

        for (String key : in.keySet()) {
            // key is table
            Set<String> existingKeys = this.getIDSet(key);

            Set<String> newKeys = in.get(key);

            newKeys.removeAll(existingKeys);

            out.put(key, newKeys);
        }

        return out;
    }

    // End private, begin package-public

    // return the full set of all UUID's present in tableName
    Set<String> getIDSet(String tableName) {
        Set<String> toReturn = new HashSet<>();

        String colName = BaseColumns._ID;

        String selectQuery = "SELECT * FROM " + tableName;
        Cursor cursor = dbr.rawQuery(selectQuery, new String[] {});

        while (cursor.moveToNext()) {
            toReturn.add(cursor.getString(cursor.getColumnIndex(colName)));
        }

        cursor.close();

        return toReturn;
    }

    // end package-public, begin public

    public void updateLocal(Map<String, Map<String, DatabaseModel>> input) {
        Map<String, Set<String>> keysToAdd = getIdsToAdd(input);

        for (String table : keysToAdd.keySet()) {
            for (String id : keysToAdd.get(table)) {
                DatabaseModel toAdd = input.get(table).get(id);

                ContentValues values = toAdd.getContentValues();

                this.insertToDb(table, null, values);
            }
        }
    }

    public Codes login(SQLiteDatabase db, String phone, String password){
        String selectQuery = "SELECT * FROM " + UserContract.TABLE_NAME + " WHERE "
                + UserContract.COLUMN_NAME_PHONE_NUMBER + " = " + phone;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        if (cursor.getCount() == 0) {
            //Incorrect phone number
            cursor.close();
            return Codes.LoginBadPhone;
        }

        cursor.moveToFirst();
        byte[] salt = cursor.getBlob(cursor.getColumnIndex(UserContract.COLUMN_NAME_SALT));
        byte[] saltedPsd = cursor.getBlob(cursor.getColumnIndex(UserContract.COLUMN_NAME_PASSWORD));

        if (!Passwords.isExpectedPassword(password.toCharArray(), salt, saltedPsd)) {
            //Incorrect password
            cursor.close();
            return Codes.LoginBadPassword;
        }

        User x = (User) ModelFactory.getExistingModel(cursor.getString(cursor.getColumnIndex(UserContract._ID)), db, UserContract.TABLE_NAME);

        // attach user to prefs
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(x); // myObject - instance of MyObject
        prefsEditor.putString(DBUtility.USER_KEY, json);
        prefsEditor.apply();
        prefsEditor.commit();

        cursor.close();
        return Codes.LoginSuccess;
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
        Gson gson = new Gson();
        JSONObject tree = new JSONObject();
        try {
            for (String tableName : hmap.keySet()) {
                HashMap<String, DatabaseModel> currentTable = hmap.get(tableName);
                JSONObject current = new JSONObject();
                for (String id : currentTable.keySet()) {
                    DatabaseModel tableEntry = currentTable.get(id);
                    String tableEntryString = gson.toJson(tableEntry);
                    JSONObject objectData = new JSONObject(tableEntryString);
                    current.put(id, objectData);
                }
                tree.put(tableName, current);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        String json = tree.toString();

        newHash.put(MSG_CONTEXT_ACCESSOR, json);

        return newHash;
    }

    public void insertToDb(String table, String nullColumnHack, ContentValues content) {

        try {
            Bridgefy.sendBroadcastMessage(this.dataToHashmap());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            // do nothing
            // jank but i've been awake for 30 hours
        }

        db.insert(table, nullColumnHack, content);

        DatabaseModel mod = ModelFactory.getModel(content);

        if (this.isConnected()) {
            DBUtility.addToFirebase(table, mod);
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
