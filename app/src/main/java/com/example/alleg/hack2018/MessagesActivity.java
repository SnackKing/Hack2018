package com.example.alleg.hack2018;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import com.example.alleg.hack2018.models.User;
import com.example.alleg.hack2018.utility.DBUtility;
import com.example.alleg.hack2018.utility.Secrets;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity implements PublicTab.OnFragmentInteractionListener, InboxTab.OnFragmentInteractionListener{

     private DBUtility dbUtility;
     private MessageListener messageListener;
     private StateListener stateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString(DBUtility.USER_KEY, null);
        User user = gson.fromJson(json, User.class);
        getSupportActionBar().setTitle(user.name + "'s Messages");

        dbUtility = new DBUtility(this);

        //Always use the Application context to avoid leaks
        Bridgefy.initialize(getApplicationContext(), Secrets.API_KEY, new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                Log.d("Bridgefy","onRegistrationSuccessful: device evaluation "+bridgefyClient.getDeviceProfile().getDeviceEvaluation());

                // Bridgefy is ready to start
                messageListener = new MessageListener() {
                    @Override
                    public void onMessageReceived(com.bridgefy.sdk.client.Message message) {
                        super.onMessageReceived(message);

                        dbUtility.updateLocal(DBUtility.getMessageContent(message));
                        Log.d("Bridgefy","Message Received");
                    }
                };

                stateListener = new StateListener() {
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
                Log.d("bridge FAIL", message);
            }});

        final ViewPager viewPager = findViewById(R.id.pager);
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

        FloatingActionButton fab = findViewById(R.id.fab);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.refresh_toolbar);
        tb.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                //clear shared preferences
                SharedPreferences mPrefs = PreferenceManager
                        .getDefaultSharedPreferences(this);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.clear();
                prefsEditor.commit();
                Intent intent = new Intent(MessagesActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_refresh:
                Toast.makeText(this,"Refreshing", Toast.LENGTH_SHORT).show();
                this.recreate();
                return true;

        }
        return true;
    }

}
