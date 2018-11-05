package com.example.alleg.hack2018.utility;

import android.provider.BaseColumns;

import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.ItemContract;
import com.example.alleg.hack2018.contracts.InventoryContract;

public class Queries {

    // types
    public final static String INT = "INT";
    public final static String BYTE_ARR = "BLOB";
    public final static String STRING = "TEXT";
    public final static String BOOL = "INT";

    // private utility class
    private Queries() {}

    public static String getCreateTableStatement(String tableName, TableField[] fields) {
        String statement = "CREATE TABLE " + tableName + " ( ";

        statement += BaseColumns._ID + " TEXT PRIMARY KEY, ";

        for (int i = 0; i < fields.length; i++) {
            TableField f = fields[i];
            statement += f.getSchemaStatement();

            if (i < fields.length - 1) {
                statement += ", ";
            } else {
                // only for last statement
                statement += ");";
            }
        }

        return statement;
    }

    public static String getDropTableStatement(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName + ";";
    }

}
