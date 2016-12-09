package com.example.mahadi.edushare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import org.spongycastle.util.encoders.Hex;

import java.util.ArrayList;

import io.left.chatmodel.ChatMessage;
import io.left.jmesh.id.MeshID;
import io.left.jmesh.utility.MeshUtility;

public class MessageList extends AppCompatActivity {

    MeshID puuid;
    ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String fromuuid = intent.getStringExtra("fromuuid");
        byte[] frombytes = Hex.decode(fromuuid.substring(2));

        puuid = new MeshID();
        puuid.setRawUuid(frombytes);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        //update the title with who we are chatting to
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Convo with " + puuid);

        if(PeerList.model == null)
        {
            MeshUtility.Log(this.getClass().getCanonicalName(), "MODEL NULL");
            return;
        }

        //allocate the incoming message queue
        ArrayList<ChatMessage> queue = PeerList.model.incoming.get(puuid);
        if(queue == null) {
            queue = new ArrayList<ChatMessage>();
            PeerList.model.incoming.put(puuid, queue);
        }

        //allocate outgoing message queue
        ArrayList<ChatMessage> oqueue = PeerList.model.outgoing.get(puuid);
        if(oqueue == null) {
            oqueue = new ArrayList<ChatMessage>();
            PeerList.model.outgoing.put(puuid, oqueue);
        }

        //allocate conversation message queue (incoming & outgoing)
        ArrayList<ChatMessage> convo = PeerList.model.conversations.get(puuid);
        if(convo == null) {
            convo = new ArrayList<ChatMessage>();
            PeerList.model.conversations.put(puuid, convo);
        }

        //connect the message queue from the model with the list view
        adapter = new ChatMessageAdapter(this, convo);
        ListView listView = (ListView) findViewById(R.id.lstMessages);
        listView.setAdapter(adapter);

        PeerList.model.messageAdapter = adapter;
        PeerList.model.messageActivity = this;
    }

    /**
     * Called by the UI when the user clicks the send button
     * @param v UI base component, usually the layout. Required by the way these callback functions works. Not used in this function currently
     */
    public void sendMessage(View v)
    {
        TextView txtMessage = (TextView)findViewById(R.id.txtInputMessage);
        String message = txtMessage.getText().toString();
        System.out.println("SENDING MSG: " + message + " to " + puuid + " from " + PeerList.model.getUuid());

        //hide keyboard & clear text
        txtMessage.setText("");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        ChatMessage msg = new ChatMessage();
        msg.to_uuid = puuid;
        msg.from_uuid = PeerList.model.getUuid();
        msg.message = message;
        msg.beenDelivered = false;
        msg.beenRead = false;

        ArrayList<ChatMessage> oqueue = PeerList.model.outgoing.get(puuid);
        if(oqueue == null) {
            oqueue = new ArrayList<ChatMessage>();
            PeerList.model.outgoing.put(puuid, oqueue);
        }
        oqueue.add(msg);

        ArrayList<ChatMessage> convo = PeerList.model.conversations.get(puuid);
        if(convo == null) {
            convo = new ArrayList<ChatMessage>();
            PeerList.model.conversations.put(puuid, convo);
        }
        convo.add(msg);

        System.out.println("SENT SHOULD BE L: " + msg.beenDelivered);

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
