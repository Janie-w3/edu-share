package com.example.mahadi.edushare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        Button transferText = (Button) findViewById(R.id.message),
                transferContent = (Button) findViewById(R.id.content),
                call = (Button) findViewById(R.id.call);

        transferText.setOnClickListener(this);
        transferContent.setOnClickListener(this);
        call.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        Intent intent;

        switch ( view.getId() ) {
            case R.id.message:
                intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
                break;

            case R.id.content:
                intent = new Intent(MainActivity.this, TransferFile.class);
                startActivity(intent);
                break;

            case R.id.call:

                break;
        }
    }
}
