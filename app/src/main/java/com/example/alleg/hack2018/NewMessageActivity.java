package com.example.alleg.hack2018;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.alleg.hack2018.models.User;
import com.example.alleg.hack2018.utility.DBUtility;
import com.google.gson.Gson;

public class NewMessageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        TextView phone = findViewById(R.id.recipient_phone);
        TextView message = findViewById(R.id.message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addToDatabase(phone.getText().toString(), message.getText().toString());
            }
        });
    }
    //TODO Implement
    private void addToDatabase(String phone, String message){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(DBUtility.USER_KEY, null);
        User user = gson.fromJson(json, User.class);
    }

}
