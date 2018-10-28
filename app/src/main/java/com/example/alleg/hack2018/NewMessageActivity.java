package com.example.alleg.hack2018;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addToDatabase("-1", message.getText().toString());
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

    private void addToDatabase(String phone, String message){
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString(DBUtility.USER_KEY, null);
        User user = gson.fromJson(json, User.class);

        DBUtility util = new DBUtility(getApplicationContext());

        ContentValues values = new ContentValues();

        values.put(MessageContract.Message.COLUMN_NAME_USER_ID, user.id);
        values.put(MessageContract.Message.COLUMN_NAME_DESTINATION_ID, phone);
        values.put(MessageContract.Message.COLUMN_NAME_MESSAGE, message);
        values.put(MessageContract.Message._ID, String.valueOf(UUID.randomUUID()));
        values.put(MessageContract.Message.COLUMN_NAME_TIME, DBUtility.getCurrentTime());

        util.insertToDb(MessageContract.Message.TABLE_NAME, null, values);
    }

}
