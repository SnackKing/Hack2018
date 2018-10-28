package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

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

    public SyncThread(DBUtility par, SQLiteDatabase db) {
        parent = par;
        this.db = db;
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

        notInCloud.remove(cloud);
        notInLocal.remove(local);

        for (String id : notInLocal) {
            Inventory temp = (Inventory) DBUtility.getRecord(InventoryContract.Inventory.TABLE_NAME, id);

            ContentValues values = new ContentValues();

            values.put(InventoryContract.Inventory._ID, temp.id);
            values.put(InventoryContract.Inventory.COLUMN_NAME_COUNT, temp.count);
            values.put(InventoryContract.Inventory.COLUMN_NAME_ITEM, temp.item);
            values.put(InventoryContract.Inventory.COLUMN_NAME_USER_ID, temp.userId);

            this.parent.insertToDb(InventoryContract.Inventory.TABLE_NAME, temp.id, null, values);
        }


        cloud = DBUtility.getIDSetCloud(ItemContract.Item.TABLE_NAME);
        local = parent.getIDSet(ItemContract.Item.TABLE_NAME);

        notInCloud = new HashSet<>(local);
        notInLocal = new HashSet<>(cloud);

        notInCloud.remove(cloud);
        notInLocal.remove(local);

        for (String id: notInLocal) {
            Item temp = (Item) DBUtility.getRecord(ItemContract.Item.TABLE_NAME, id);

            ContentValues values = new ContentValues();

            values.put(ItemContract.Item._ID, temp.id);
            values.put(ItemContract.Item.COLUMN_NAME_IMPORTANCE, temp.importance);
            values.put(ItemContract.Item.COLUMN_NAME_NAME, temp.name);
            values.put(ItemContract.Item.COLUMN_NAME_PERISHABLE, temp.perishable);

            this.parent.insertToDb(ItemContract.Item.TABLE_NAME, temp.id, null, values);
        }



        cloud = DBUtility.getIDSetCloud(MessageContract.Message.TABLE_NAME);
        local = parent.getIDSet(MessageContract.Message.TABLE_NAME);

        notInCloud = new HashSet<>(local);
        notInLocal = new HashSet<>(cloud);

        notInCloud.remove(cloud);
        notInLocal.remove(local);

        for (String id: notInLocal) {
            Message temp = (Message) DBUtility.getRecord(MessageContract.Message.TABLE_NAME, id);

            ContentValues values = new ContentValues();

            values.put(MessageContract.Message._ID, temp.id);
            values.put(MessageContract.Message.COLUMN_NAME_TIME, temp.time);
            values.put(MessageContract.Message.COLUMN_NAME_MESSAGE, temp.msg);
            values.put(MessageContract.Message.COLUMN_NAME_DESTINATION_ID, temp.recipId);
        }
    }
}
