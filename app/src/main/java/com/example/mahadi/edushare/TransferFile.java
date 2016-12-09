package com.example.mahadi.edushare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.left.jmesh.android.AndroidMeshManager;
import io.left.jmesh.id.MeshID;
import io.left.jmesh.mesh.DataListener;
import io.left.jmesh.mesh.MeshManager;
import io.left.jmesh.mesh.MeshStateListener;


public class TransferFile extends AppCompatActivity implements View.OnClickListener, MeshStateListener, DataListener {

    MeshID uuid;
    HashSet<MeshID> peers;
    public static final int MESH_PORT = 1111;
    AndroidMeshManager amm;
    ImagePicker imagePicker;
    private MeshIdAdapter idAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(getApplicationContext());

        setContentView(R.layout.transfer_file);

        if (amm == null) {
            peers = new HashSet<>();

            // create a manual mode mesh manager
            amm = new AndroidMeshManager(this, this);
            amm.registerDataListener(this, MESH_PORT);

            // init Adapter to set MeshIds to view
            idAdapter = new MeshIdAdapter(this);

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(idAdapter);
        }
    }

    @Override
    public void init(MeshID uuid, int state) {
        this.uuid = uuid;

        //File transfer
        Button btnImg = (Button) findViewById(R.id.btnImage);
        btnImg.setEnabled(true);
        btnImg.setOnClickListener(this);
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

        if (view.getId() == R.id.btnImage) {
            selectImage(view);
        }
    }

    /**
     * Called when a bind completes
     *
     * @param status success or fail
     */
    public void bindResult(int status, String message) {
        if (status != MeshManager.BIND_SUCCESS)
            Toast.makeText(getApplicationContext(), "Error binding to mport: " + message, Toast.LENGTH_SHORT).show();
    }

    public void selectImage(View v) {

        final MeshID targetMeshId = getMeshId(idAdapter.getSelectedItem());

        if (targetMeshId == null) {
            NotifyUtil.shortToast(this, "Select valid contact");
            return;
        }

        imagePicker = new ImagePicker(this);

        imagePicker.setImagePickerCallback(
                new ImagePickerCallback() {
                    @Override
                    public void onImagesChosen(List<ChosenImage> images) {
                        Iterator<ChosenImage> it = images.iterator();
                        while (it.hasNext()) {
                            ChosenImage i = it.next();
                            Log.d("MA", i.getOriginalPath());
                            File image = new File(i.getOriginalPath());
                            byte[] filedata = new byte[(int) image.length()];

                            try {
                                DataInputStream dis = new DataInputStream(new FileInputStream(image));
                                dis.readFully(filedata);
                                dis.close();
                                Log.d("MA", "Read " + filedata.length + " bytes");

                            } catch (Exception e) {
                                Log.d("MA", "Failed to read the file: " + e.toString());
                            }

                            ByteBuffer imageBuffer = ByteBuffer.allocate(filedata.length);
                            //imageBuffer.putInt(filedata.length);
                            imageBuffer.put(filedata);

                            amm.send_data_reliable(targetMeshId, MESH_PORT, imageBuffer.array());
                            //amm.send_data_unreliable(mm.getUuid(), imageBuffer.array());
                            //amm.send_data_reliable(randomPeer(), imageBuffer.array());
                        }
                    }

                    @Override
                    public void onError(String message) {
                        // Do error handling
                    }
                }
        );

        // imagePicker.allowMultiple(); // Default is false
        // imagePicker.shouldGenerateMetadata(false); // Default is true
        // imagePicker.shouldGenerateThumbnails(false); // Default is true

        imagePicker.pickImage();
    }

    public void sendText(View v)
    {
        String deviceName = android.os.Build.MODEL;

        Toast.makeText(getApplicationContext(), "Hello " + deviceName, Toast.LENGTH_SHORT).show();

        final MeshID targetMeshId = getMeshId(idAdapter.getSelectedItem());

        if (targetMeshId == null) {
            NotifyUtil.shortToast(this, "Select valid contact");
            return;
        }

        Toast.makeText(getApplicationContext(), "Hello Mahadi", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            }
        }
    }

    /**
     * Callback function so that the network layer can update the model
     *
     * @param puuid the unique id of the peer that has changed
     * @param state is the peer being added, removed or updated
     */
    public void peerChanged(MeshID puuid, int state) {
        switch (state) {

            case DataListener.ADDED:
            case DataListener.UPDATED:
                peers.add(puuid);
                loadMeshIds();
                break;

            case DataListener.REMOVED:
                peers.remove(puuid);
                loadMeshIds();
                break;
        }
    }

    private void loadMeshIds() {

        String meshId = idAdapter.getSelectedItem();

        idAdapter.clear();

        for (MeshID meshID : peers) {
            //if (!meshID.equals(amm.getUuid())) {
            idAdapter.addItem(meshID.toString());
            // }
        }

        if (meshId != null && meshId.length() > 0) {
            idAdapter.targetSelection(meshId);
        }
    }

    private MeshID getMeshId(String targetMeshId) {
        for (MeshID meshID : peers) {
            if (meshID.toString().equals(targetMeshId)) {
                return meshID;
            }
        }
        return null;
    }

    /*
     * Returns a random peer id - returns ourselve only if we don't exist
     */
    private MeshID randomPeer() {
        if (peers.size() == 1)
            return uuid;
        else {
            int index = new Random().nextInt(peers.size());
            int i = 0;
            Log.d("MA", "RANDOM INDEX: " + index);
            for (MeshID peer : peers) {
                if (i == index)
                    return peer;
                i = i + 1;
            }
        }
        return uuid;
    }

    /*
     * Used to receive images
     */
    public void dataReceived(MeshID puuid, byte[] data) {
        System.out.println("RECEIVED FROM: " + puuid + " " + data.length + " bytes of data");

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        String filename = "meshfile_" + ts + ".jpg";

        try {
            File meshDir = new File(Environment.getExternalStorageDirectory(), "Mesh" + File.separator);
            if (!meshDir.exists()) {
                meshDir.mkdirs();
            }
            File file = new File(meshDir, filename);
            file.createNewFile();
            if(file.exists()) {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.write(data);
                bos.flush();
                bos.close();
                NotifyUtil.shortToast(getApplicationContext(), filename + " saved");
                setImage(file.getPath());
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setImage(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDraweeView view = (SimpleDraweeView) findViewById(R.id.imgDisplay);

                Uri imageUri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_FILE_SCHEME)
                        .path(path)
                        .build();

                view.setImageURI(imageUri);
            }
        });

    }
}
