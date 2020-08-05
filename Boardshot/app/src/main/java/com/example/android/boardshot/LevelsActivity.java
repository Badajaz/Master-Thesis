package com.example.android.boardshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LevelsActivity extends AppCompatActivity {

    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Button btnLevel1 = (Button) findViewById(R.id.buttonLevel1Voz);



        btnLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                intent.putExtra("levels", "level1Voz");
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = bundle.getString("user");
        Toast.makeText(getApplicationContext(),user,Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);

    }
}
