package com.example.alleg.hack2018.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.alleg.hack2018.models.DatabaseModel;
import com.example.alleg.hack2018.models.ModelFactory;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 108;
    private static final String DATABASE_NAME = "Disaster.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        for (DatabaseModel model : ModelFactory.DEFAULTS) {
            db.execSQL(model.getCreateTable());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        for (DatabaseModel model : ModelFactory.DEFAULTS) {
            db.execSQL(model.getDropTable());
        }

        onCreate(db);
    }
}