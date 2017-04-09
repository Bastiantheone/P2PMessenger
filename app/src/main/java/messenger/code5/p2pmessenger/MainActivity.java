package com.example.troyb.messenger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    ArrayList<Message> messages;
    private static int lastMsgId=0;
   public Button sendButton;
    public EditText mText;
    public RecyclerView rvMessages;
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);

        // Initialize contacts
        messages = Message.createMessagesList(0);

        // Set layout manager to position the items
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        // Create adapter passing in the sample user data
        MessageAdapter adapter = new MessageAdapter(this, messages);
        // Attach the adapter to the recyclerview to populate items
        rvMessages.setAdapter(adapter);

        sendButton = (Button) findViewById(R.id.sendButton);
        mText = (EditText) findViewById(R.id.typeMessage) ;
        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

               String mesg = mText.getText().toString();
                messages.add((new Message(mesg)));
                MessageAdapter adapter = new MessageAdapter(getBaseContext(), messages);
                rvMessages.setAdapter(adapter);


           }
        });
    }
}
