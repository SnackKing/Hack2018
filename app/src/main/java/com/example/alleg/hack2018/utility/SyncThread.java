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
        while (true) {
            // only check for internet every minute
            try {
                this.sleep(60000);

                if (parent.isConnected()) {
                    this.fullSyncToCloud();
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
    }
}
