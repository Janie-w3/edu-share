package com.example.mahadi.edushare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.left.chatmodel.ChatPeer;

/**
 * Created by jason on 07/06/16.
 */
public class ChatPeerAdapter extends ArrayAdapter<ChatPeer> {
    public ChatPeerAdapter(Context context, List<ChatPeer> chatPeers)
    {
        super(context, 0, chatPeers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatPeer peer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_peer, parent, false);
        }
        // Lookup view for data population
        TextView peerid = (TextView) convertView.findViewById(R.id.peer_status);
        // Populate the data into the template view using the data object
        peerid.setText(String.valueOf(peer.uuid));

        // Return the completed view to render on screen
        return convertView;
    }
}
