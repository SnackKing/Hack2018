package com.example.alleg.hack2018.utility;

import android.database.sqlite.SQLiteDatabase;

import com.example.alleg.hack2018.contracts.InventoryContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.models.Inventory;
import com.example.alleg.hack2018.models.Item;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.User;

public class SyncThread extends Thread {

    DBUtility parent;
    SQLiteDatabase db;

    public SyncThread(DBUtility par, SQLiteDatabase db) {
        parent = par;
        this.db = db;
    }

    @Override
    public void run() {
        // this is a constant.
        // 5 * this = number of seconds till checking to sync entire db
        int iterationsLimit = 60;

        int iterations = 0;

        while (!parent.isConnected()) {
            // only check for internet every 5 seconds
            try {
                iterations ++;
                this.sleep(5000);

                if (iterations % iterationsLimit == 0 ) {
                    iterations = 0;
                    this.fullSyncToCloud();
                }
            } catch (java.lang.InterruptedException e) {
                // good! we can process now
                // do nothing
            }
        }

        // empty buffers
        syncToCloud();
    }

    private void fullSyncToCloud() {

    }

    // TODO : save changes and deletions
    private void syncToCloud() {
        // empty each of the not sent arrays

        while (DBUtility.notSentMessages.size() > 0 ) {
            Message msg = DBUtility.notSentMessages.remove(DBUtility.notSentMessages.size() - 1);

            DBUtility.myRef.child(MessageContract.Message.TABLE_NAME).child(String.valueOf(msg.id)).setValue(msg);
        }

        while (DBUtility.notSentUsers.size() > 0) {
            User usr = DBUtility.notSentUsers.remove(DBUtility.notSentUsers.size() - 1);

            DBUtility.myRef.child(UserContract.User.TABLE_NAME).child(String.valueOf(usr.id)).setValue(usr);
        }

        while (DBUtility.notSentInventories.size() > 0) {
            Inventory i = DBUtility.notSentInventories.remove(DBUtility.notSentInventories.size() - 1);

            DBUtility.myRef.child(InventoryContract.Inventory.TABLE_NAME).child(String.valueOf(i.id)).setValue(i);
        }

        while (DBUtility.notSentItems.size() > 0) {
            Item i = DBUtility.notSentItems.remove(DBUtility.notSentItems.size() - 1);

            DBUtility.myRef.child(ItemContract.Item.TABLE_NAME).child(String.valueOf(i.id)).setValue(i);
        }
    }

}
