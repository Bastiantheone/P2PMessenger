package messenger.code5.p2pmessenger;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by troyb on 4/6/2017.
 */

public class Message extends Activity {

      private String msg;
        private String color;
    private int id;

public Message(String message, int id){
        msg=message;
    this.id=id;

}
        public String getMsg() {
                return msg;
        }

        public void setMsg(String msg) {
                this.msg = msg;
        }

        public int getId(){
            return id;
        }

        public String getColor() {
                return color;
        }

        public void setColor(String color) {
                this.color = color;
        }

}
