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
        String[] tableNames = { InventoryContract.Inventory.TABLE_NAME, ItemContract.Item.TABLE_NAME,
                                MessageContract.Message.TABLE_NAME, UserContract.User.TABLE_NAME};

        for (String table : tableNames) {
            Set<String> cloud = DBUtility.getIDSetCloud(table);
            Set<String> local = parent.getIDSet(table);

            Set<String> notInCloud = new HashSet<>(local);
            Set<String> notInLocal = new HashSet<>(cloud);

            notInCloud.remove(cloud);
            notInLocal.remove(local);
        }
    }
}
