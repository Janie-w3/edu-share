package com.example.mahadi.edushare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;

import io.left.jmesh.android.AndroidMeshManager;
import io.left.jmesh.id.MeshID;
import io.left.jmesh.mesh.DataListener;
import io.left.jmesh.mesh.MeshManager;
import io.left.jmesh.mesh.MeshStateListener;

public class UserActivity extends AppCompatActivity implements View.OnClickListener, MeshStateListener, DataListener {

    private static final int MESH_PORT = 5876;
    private AndroidMeshManager amm;

    private final HashSet<MeshID> allPeers = new HashSet<>();
    private MeshIdAdapter idAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (amm == null) {
            amm = new AndroidMeshManager(this, this);
            amm.registerDataListener(this, MESH_PORT);
        }

        // init Adapter to set MeshIds to view
        idAdapter = new MeshIdAdapter(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(idAdapter);

        findViewById(R.id.buttonSend).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_configure) {
            amm.showSettingsActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        amm.stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.layoutId) {
            int position = (int) view.getTag();

            String meshId = idAdapter.getItem(position);

            if (amm.getUuid().toString().equals(meshId)) {
                NotifyUtil.shortToast(getApplicationContext(), "It's You");
                return;
            }

            idAdapter.targetSelection(position);
        }

        if (view.getId() == R.id.buttonSend) {

            MeshID targetMeshId = getMeshId(idAdapter.getSelectedItem());

            if (targetMeshId != null) {

                String message = ((EditText) findViewById(R.id.editMessage)).getText().toString();

                if (message.length() > 0) {

                    String data = message + " from " + amm.getUuid().toString();

                    amm.send_data_reliable(targetMeshId, MESH_PORT, data.getBytes());

                    ((EditText) findViewById(R.id.editMessage)).setText(null);
                }
            } else {
                NotifyUtil.shortToast(getApplicationContext(), "User Not Reachable");
            }

        }
    }

    @Override
    public void init(MeshID meshID, int state) {
        if (state != MeshStateListener.SUCCESS) {
            NotifyUtil.shortToast(getApplicationContext(), "Error initializing the library");
        }
    }

    @Override
    public void peerChanged(MeshID meshID, int state) {

        switch (state) {

            case DataListener.ADDED:
            case DataListener.UPDATED:
                allPeers.add(meshID);
                loadMeshIds();
                break;

            case DataListener.REMOVED:
                allPeers.remove(meshID);
                loadMeshIds();
                break;
        }

        //MeshUtility.Log("Mesh", "peerChanged + " + state + "  " + meshID.toString());

    }

    @Override
    public void dataReceived(MeshID meshID, byte[] bytes) {
        String message = new String(bytes);

        NotifyUtil.shortToast(getApplicationContext(), message);
    }

    @Override
    public void bindResult(int status, String message) {
        if (status != MeshManager.BIND_SUCCESS) {
            NotifyUtil.shortToast(getApplicationContext(), "Error binding to mport: " + message);
        }
    }


    private void loadMeshIds() {

        String meshId = idAdapter.getSelectedItem();

        idAdapter.clear();

        for (MeshID meshID : allPeers) {
            //if (!meshID.equals(amm.getUuid())) {
                idAdapter.addItem(meshID.toString());
           // }
        }

        if (meshId != null && meshId.length() > 0) {
            idAdapter.targetSelection(meshId);
        }
    }

    private MeshID getMeshId(String targetMeshId) {
        for (MeshID meshID : allPeers) {
            if (meshID.toString().equals(targetMeshId)) {
                return meshID;
            }
        }
        return null;
    }

}
