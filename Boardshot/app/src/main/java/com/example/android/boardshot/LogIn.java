package com.example.android.boardshot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LogIn extends AppCompatActivity {

    private DatabaseReference database;
    private StorageReference mStorageRef;
    User u = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Button btnLogIn = (Button) findViewById(R.id.buttonLogIn);
        database = FirebaseDatabase.getInstance().getReference().child("Users");




        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userName = (EditText) findViewById(R.id.userNameEditText);
                String userNameStr = userName.getText().toString();
                u.setName(userNameStr);
                u.setPoints(0);
                u.setLevels("");
                database.child(userNameStr).setValue(u);
                Intent intent = new Intent(LogIn.this, LevelsActivity.class);
                intent.putExtra("user", userNameStr);
                startActivity(intent);
            }
        });


    }
}
