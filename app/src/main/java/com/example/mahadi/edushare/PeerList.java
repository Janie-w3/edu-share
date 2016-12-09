package com.example.mahadi.edushare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.left.chatmodel.ChatModel;

public class PeerList extends AppCompatActivity {

    public static ChatModel model;
    ChatPeerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_list);

        if(model == null) {
            model = new ChatModel(this);
            adapter = new ChatPeerAdapter(this, PeerList.model.peers);
            ListView listView = (ListView) findViewById(R.id.lstPeers);
            listView.setAdapter(adapter);
            PeerList.model.peerAdapter = adapter;
            PeerList.model.peerActivity = this;

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RelativeLayout layout = (RelativeLayout)view;
                    TextView text = (TextView)layout.findViewById(R.id.peer_status);
                    String item = text.getText().toString();
                    System.out.println("CLICKED: " + item);

                    TextView text2 = (TextView)layout.findViewById(R.id.peerid);
                    String name = text2.getText().toString();

                    Intent myIntent = new Intent(PeerList.this, MessageList.class);
                    myIntent.putExtra("fromuuid",item);
                    //myIntent.putExtra("name",name);

                    PeerList.this.startActivity(myIntent);
                }
            });

            /*
            final Handler clock = new Handler();
            clock.post(new Runnable() {
                @Override public void run() {
                    TextView text = (TextView)findViewById(R.id.txtStatus);
                    text.setText(model.statusText);
                    // 50 millis to give the ui thread time to breath. Adjust according to your own experience
                    clock.postDelayed(this, 50);
                }
            });*/
        }
    }

    public void meshSettings(View v)
    {
        model.settings();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        model.stop();
    }
}
