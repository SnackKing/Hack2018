package com.example.alleg.hack2018;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.User;
import com.example.alleg.hack2018.utility.DBHelper;
import com.example.alleg.hack2018.utility.DBUtility;
import com.example.alleg.hack2018.utility.MessageListAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class MessagesActivity extends AppCompatActivity implements PublicTab.OnFragmentInteractionListener, InboxTab.OnFragmentInteractionListener{

     private DBUtility dbUtility;
     private String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        API_KEY = "b1109f22-fb9e-4e07-a2d2-6197ca1ee2eb";

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString(DBUtility.USER_KEY, null);
        User user = gson.fromJson(json, User.class);
        getSupportActionBar().setTitle(user.name + "'s Messages");

        dbUtility = new DBUtility(this);

        //Always use the Application context to avoid leaks
        Bridgefy.initialize(getApplicationContext(), API_KEY, new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                // Bridgefy is ready to start
                MessageListener messageListener = new MessageListener() {
                    @Override
                    public void onMessageReceived(com.bridgefy.sdk.client.Message message) {
                        super.onMessageReceived(message);
                        HashMap hmap = message.getContent();
                        //dbUtility.updateLocal(hmap);
                        Log.d("Bridgefy","Message Received");
                    }
                };
                StateListener stateListener = new StateListener() {
                    @Override
                    public void onDeviceConnected(Device device, Session session) {
                        super.onDeviceConnected(device, session);
                        com.bridgefy.sdk.client.Message message =new com.bridgefy.sdk.client.Message.Builder().setContent(dbUtility.dataToHashmap()).setReceiverId(device.getUserId()).build();
                        Bridgefy.sendMessage(message);
                        Log.d("Bridgefy","Message Sent to New Connection");

                    }

                };
                Bridgefy.start(messageListener, stateListener);
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                // Something went wrong: handle error code, maybe print the message

            }});

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newMessageIntent = new Intent(MessagesActivity.this,NewMessageActivity.class);
                startActivity(newMessageIntent);
            }
        });

//        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
//        this.mDbHelp = new DBHelper(this);
//        messageList = Message.getPublicMessages(mDbHelp.getReadableDatabase());
//        mMessageAdapter = new MessageListAdapter(this, messageList);
//        mMessageRecycler.setAdapter(mMessageAdapter);
//        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public void onResume(){
        super.onResume();
//        messageList = Message.getPublicMessages(mDbHelp.getReadableDatabase());
//        mMessageAdapter = new MessageListAdapter(this, messageList);
//        mMessageAdapter.notifyDataSetChanged();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
