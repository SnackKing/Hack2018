package com.example.alleg.hack2018;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract;
import com.example.alleg.hack2018.models.User;
import com.example.alleg.hack2018.utility.DBHelper;
import com.example.alleg.hack2018.utility.DBUtility;
import com.google.gson.Gson;

import java.util.UUID;

public class NewMessageActivity extends AppCompatActivity {
    private DBHelper mDbHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        final TextView message = findViewById(R.id.message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addNewMessageToDatabase("-1", message.getText().toString());
               Log.d("FLOAT", "starting Activity");
                Intent intent = new Intent(NewMessageActivity.this, MessagesActivity.class);
                startActivity(intent);
            }
        });
        this.mDbHelp = new DBHelper(this);
    }

    @Override
    protected void onDestroy() {
        mDbHelp.close();

        super.onDestroy();
    }

    private void addNewMessageToDatabase(String phone, String message){
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString(DBUtility.USER_KEY, null);
        User user = gson.fromJson(json, User.class);

        DBUtility util = new DBUtility(getApplicationContext());

        ContentValues values = new ContentValues();

        String newId = null;

        if (phone.equals(DBUtility.PUBLIC_MESSAGE_DEST)) {
            newId = DBUtility.PUBLIC_MESSAGE_DEST;
        } else {
            String selectQuery = "SELECT * FROM " + UserContract.TABLE_NAME
                + " WHERE " + UserContract.COLUMN_NAME_PHONE_NUMBER + " = \"" + phone + "\"";
            Cursor cursor = mDbHelp.getReadableDatabase().rawQuery(selectQuery, new String[] {});

            if(cursor.getCount() == 0) {
                //No destination phone found
                //TODO
            } else {
                newId = cursor.getString(cursor.getColumnIndex(UserContract._ID));
            }

            cursor.close();
        }

        values.put(MessageContract.COLUMN_NAME_USER_ID, user.getID());
        values.put(MessageContract.COLUMN_NAME_DESTINATION_ID, newId);
        values.put(MessageContract.COLUMN_NAME_MESSAGE, message);
        values.put(MessageContract._ID, String.valueOf(UUID.randomUUID()));
        values.put(MessageContract.COLUMN_NAME_TIME, DBUtility.getCurrentTime());

        util.insertToDb(MessageContract.TABLE_NAME, null, values);
    }

}
