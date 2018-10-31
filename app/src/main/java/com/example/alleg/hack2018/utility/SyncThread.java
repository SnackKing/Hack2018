package com.example.alleg.hack2018.utility;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
                sleep(nextSleep);

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

        String[] tables = new String[] {InventoryContract.TABLE_NAME, ItemContract.TABLE_NAME, UserContract.TABLE_NAME, MessageContract.TABLE_NAME};

        for (String table : tables) {

            Set<String> cloud = DBUtility.getIDSetCloud(table);
            Set<String> local = parent.getIDSet(table);

            Set<String> notInCloud = new HashSet<>(local);
            Set<String> notInLocal = new HashSet<>(cloud);

            notInCloud.removeAll(cloud);
            notInLocal.removeAll(local);

            for (String id : notInLocal) {
                DatabaseModel temp = (DatabaseModel) DBUtility.getRecordFromFirebase(table, id);

                ContentValues cv = temp.getContentValues();

                this.parent.insertToDb(table, null, cv);
            }

            for (String id : notInCloud) {
                DatabaseModel temp = ModelFactory.getExistingModel(id, dbr, table);
                DBUtility.addToFirebase(table, temp);
            }
        }
    }
}
