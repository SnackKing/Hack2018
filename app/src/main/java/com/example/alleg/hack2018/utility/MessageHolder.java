package com.example.alleg.hack2018.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.alleg.hack2018.R;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.User;
import com.google.gson.Gson;

public class MessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, nameText;

    MessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
    }

    void bind(Message message, Context context) {
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(DBUtility.USER_KEY, null);
        User user = gson.fromJson(json, User.class);
        DBHelper helper = new DBHelper(context);
        User sender = new User(message.senderId, helper.getReadableDatabase());
        messageText.setText(message.msg);
        if(sender.id.equals(user.id)){
            messageText.setBackgroundResource(R.color.colorAccent);
            nameText.setText("You");
        }

        // Format the stored timestamp into a readable String using method.


    }
}