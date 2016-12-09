package com.example.mahadi.edushare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.left.chatmodel.ChatMessage;
import io.left.jmesh.utility.MeshUtility;

/**
 * Created by jason on 08/06/16.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessages)
    {
        super(context, 0, chatMessages);
    }

    /**
     * Code for L and R view: https://trinitytuts.com/simple-chat-application-using-listview-in-android/
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatMessage message = getItem(position);
        if(message == null)
        {
            MeshUtility.Log(this.getClass().getCanonicalName(), "MESSAGE NULL");
            return null;
        }

        if(PeerList.model == null)
        {
            MeshUtility.Log(this.getClass().getCanonicalName(), "MODEL NULL");
            return null;
        }

        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(message.to_uuid.equals(PeerList.model.uuid))
        {
            System.out.println("R: " + message.message);
            row = inflater.inflate(R.layout.item_message_right, parent, false);
        }
        else
        {
            System.out.println("L" + message.message);
            row = inflater.inflate(R.layout.item_message_left, parent, false);
        }

        TextView idView = (TextView) row.findViewById(R.id.msgid);
        idView.setText(String.valueOf(message.from_uuid));

        TextView msgView = (TextView) row.findViewById(R.id.msg);
        msgView.setText(message.message);

        // Return the completed view to render on screen
        return row;
    }
}
