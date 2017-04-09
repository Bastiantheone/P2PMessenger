package com.example.troyb.messenger;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by troyb on 4/6/2017.
 */

public class Message extends Activity {

      private String msg;
        private String color;

public Message(String message){
        msg=message;

}
        public String getMsg() {
                return msg;
        }

        public void setMsg(String msg) {
                this.msg = msg;
        }

        public String getColor() {
                return color;
        }

        public void setColor(String color) {
                this.color = color;
        }



        private static int lastMsgId=0;
        public static ArrayList<Message> createMessagesList(int numMessages) {
                ArrayList<Message> messages = new ArrayList<Message>();

                for (int i = 1; i <= numMessages; i++) {
                        messages.add(new Message("Msg: " + ++lastMsgId));
                }

                return messages;
        }
}
