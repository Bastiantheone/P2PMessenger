package messenger.code5.p2pmessenger;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by troyb on 4/6/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> mMessages;
    // Store the context for easy access
    private Context mContext;
private int previousPosition=0;
    // Pass in the contact array into the constructor
    public MessageAdapter(Context context, ArrayList<Message> messages) {
        mMessages= messages;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView messageTextView;
        public CardView cardView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            messageTextView = (TextView) itemView.findViewById(R.id.messageTv);
            cardView = (CardView)itemView.findViewById(R.id.message_card_view);

        }
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       // Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View messageView = inflater.inflate(R.layout.message_view, parent, false);


        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(messageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
// Get the data model based on position
        Message message = mMessages.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.messageTextView;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(textView.getLayoutParams());
        lp.gravity= Gravity.END;
        textView.setLayoutParams(lp);
        //EditText editText  = holder.messageEditText;
       //message.setMsg(editText.getText().toString());
        textView.setText(message.getMsg());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}


