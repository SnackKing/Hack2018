package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.models.Inventory;
import com.example.alleg.hack2018.models.Item;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.User;

import java.util.HashSet;
import java.util.Set;

public class SyncThread extends Thread {

    DBUtility parent;
    SQLiteDatabase db;
    SQLiteDatabase dbr;

    public SyncThread(DBUtility par, SQLiteDatabase db, SQLiteDatabase dbr) {
        parent = par;
        this.db = db;
        this.dbr = dbr;
    }

    @Override
    public void run() {
        int nextSleep = DBUtility.MS_WAIT_THREAD_CHECK;

        while (true) {
            // only check for internet every minute
            try {
                this.sleep(nextSleep);

                if (parent.isConnected()) {
                    nextSleep = DBUtility.MS_WAIT_THREAD_CHECK;
                    this.fullSyncToCloud();
                } else {
                    // not connected - look for internet desperately
                    nextSleep = DBUtility.MS_WAIT_THREAD_CHECK / 4;
                }
            } catch (java.lang.InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fullSyncToCloud() {

        Set<String> cloud = DBUtility.getIDSetCloud(InventoryContract.Inventory.TABLE_NAME);
        Set<String> local = parent.getIDSet(InventoryContract.Inventory.TABLE_NAME);

        Set<String> notInCloud = new HashSet<>(local);
        Set<String> notInLocal = new HashSet<>(cloud);

        notInCloud.removeAll(cloud);
        notInLocal.removeAll(local);

        for (String id : notInLocal) {
            Inventory temp = (Inventory) DBUtility.getRecord(InventoryContract.Inventory.TABLE_NAME, id);

            ContentValues values = new ContentValues();

            values.put(InventoryContract.Inventory._ID, temp.id);
            values.put(InventoryContract.Inventory.COLUMN_NAME_COUNT, temp.count);
            values.put(InventoryContract.Inventory.COLUMN_NAME_ITEM, temp.item);
            values.put(InventoryContract.Inventory.COLUMN_NAME_USER_ID, temp.userId);

            this.parent.insertToDb(InventoryContract.Inventory.TABLE_NAME,null, values);
        }

        for (String id : notInCloud) {
            Inventory temp = new Inventory(id, dbr);
            DBUtility.addToFirebase(InventoryContract.Inventory.TABLE_NAME, temp);
        }

        cloud = DBUtility.getIDSetCloud(ItemContract.Item.TABLE_NAME);
        local = parent.getIDSet(ItemContract.Item.TABLE_NAME);

        notInCloud = new HashSet<>(local);
        notInLocal = new HashSet<>(cloud);

        notInCloud.removeAll(cloud);
        notInLocal.removeAll(local);

        for (String id : notInLocal) {
            Item temp = (Item) DBUtility.getRecord(ItemContract.Item.TABLE_NAME, id);

            ContentValues values = new ContentValues();

            values.put(ItemContract.Item._ID, temp.id);
            values.put(ItemContract.Item.COLUMN_NAME_IMPORTANCE, temp.importance);
            values.put(ItemContract.Item.COLUMN_NAME_NAME, temp.name);
            values.put(ItemContract.Item.COLUMN_NAME_PERISHABLE, temp.perishable);

            this.parent.insertToDb(ItemContract.Item.TABLE_NAME,null, values);
        }

        for (String id : notInCloud) {
            Item temp = new Item(id, dbr);
            DBUtility.addToFirebase(ItemContract.Item.TABLE_NAME, temp);
        }

        cloud = DBUtility.getIDSetCloud(UserContract.User.TABLE_NAME);
        local = parent.getIDSet(UserContract.User.TABLE_NAME);

        notInCloud = new HashSet<>(local);
        notInLocal = new HashSet<>(cloud);

        notInCloud.removeAll(cloud);
        notInLocal.removeAll(local);

        for (String id : notInLocal) {
            User temp = (User) DBUtility.getRecord(UserContract.User.TABLE_NAME, id);

            ContentValues values = new ContentValues();

            int res = 0;
            if (temp.resident) {
                res = 1;
            }

            values.put(UserContract.User._ID, temp.id);
            values.put(UserContract.User.COLUMN_NAME_NAME, temp.name);
            values.put(UserContract.User.COLUMN_NAME_PASSWORD, DBUtility.toByteArray(temp.password));
            values.put(UserContract.User.COLUMN_NAME_PHONE_NUMBER, temp.phoneNumber);
            values.put(UserContract.User.COLUMN_NAME_RESIDENT, res);
            values.put(UserContract.User.COLUMN_NAME_SALT, DBUtility.toByteArray(temp.salt));

            this.parent.insertToDb(UserContract.User.TABLE_NAME,null, values);

            Log.d("saved in local user", temp.id);
        }

        for (String id : notInCloud) {
            User temp = new User(id, dbr);
            DBUtility.addToFirebase(UserContract.User.TABLE_NAME, temp);
            Log.d("saved in cloud user", temp.id);
        }

        cloud = DBUtility.getIDSetCloud(MessageContract.Message.TABLE_NAME);
        local = parent.getIDSet(MessageContract.Message.TABLE_NAME);

        notInCloud = new HashSet<>(local);
        notInLocal = new HashSet<>(cloud);

        notInCloud.removeAll(cloud);
        notInLocal.removeAll(local);

        for (String id : notInLocal) {
            Message temp = (Message) DBUtility.getRecord(MessageContract.Message.TABLE_NAME, id);

            ContentValues values = new ContentValues();

            values.put(MessageContract.Message._ID, temp.id);
            values.put(MessageContract.Message.COLUMN_NAME_TIME, temp.time);
            values.put(MessageContract.Message.COLUMN_NAME_MESSAGE, temp.msg);
            values.put(MessageContract.Message.COLUMN_NAME_USER_ID, temp.senderId);
            values.put(MessageContract.Message.COLUMN_NAME_DESTINATION_ID, temp.recipId);
            values.put(MessageContract.Message.COLUMN_NAME_TIME, temp.time);

            this.parent.insertToDb(MessageContract.Message.TABLE_NAME,null, values);

            Log.d("saved locally message ", temp.id);
        }

        for (String id : notInCloud) {
            Message temp = new Message(id, dbr);
            DBUtility.addToFirebase(MessageContract.Message.TABLE_NAME, temp);
            Log.d("saved in cloud message ", temp.id);
        }
    }
}
