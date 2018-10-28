package com.example.alleg.hack2018.utility;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.alleg.hack2018.R;
import com.example.alleg.hack2018.models.Message;
import com.example.alleg.hack2018.models.User;

public class MessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, nameText;

    MessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
    }

    void bind(Message message, Context context) {
        DBHelper helper = new DBHelper(context);
        User sender = new User(message.senderId, helper.getReadableDatabase());
        messageText.setText(message.msg);

        // Format the stored timestamp into a readable String using method.
        nameText.setText(sender.name);


    }
}