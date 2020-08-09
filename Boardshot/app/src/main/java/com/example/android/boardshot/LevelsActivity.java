package com.example.android.boardshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        invalidateOptionsMenu();
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);




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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_one) {
            Intent intent = new Intent(LevelsActivity.this, EditUser.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_item_one);
        if (item.getTitle().equals("Camera")) {
            item.setTitle(user);
        }
        return super.onPrepareOptionsMenu(menu);
    }


}
