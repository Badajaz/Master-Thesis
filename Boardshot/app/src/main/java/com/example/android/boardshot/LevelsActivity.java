package com.example.android.boardshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LevelsActivity extends AppCompatActivity {

    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Button btnLevel1 = (Button) findViewById(R.id.buttonLevel1Voz);
        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        Toast.makeText(getApplicationContext(),"level strait = "+user,Toast.LENGTH_LONG).show();




        btnLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                intent.putExtra("levels", "level1Voz");
                intent.putExtra("user", user);
                startActivityForResult(intent,RESULT_OK);
            }
        });


    }

}
