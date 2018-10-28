package com.example.alleg.hack2018;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.User;
import com.example.alleg.hack2018.utility.DBHelper;
import com.example.alleg.hack2018.utility.MessageListAdapter;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity implements PublicTab.OnFragmentInteractionListener, InboxTab.OnFragmentInteractionListener{

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private DBHelper mDbHelp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



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

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        this.mDbHelp = new DBHelper(this);
        ArrayList<Message> messageList = Message.getPublicMessages(mDbHelp.getReadableDatabase());
        mMessageAdapter = new MessageListAdapter(this, messageList);

        // new User(messageList.get(0).senderId).name;

        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
