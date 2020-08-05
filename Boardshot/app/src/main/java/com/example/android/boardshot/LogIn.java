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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Button btnLogIn = (Button) findViewById(R.id.buttonLogIn);
        database = FirebaseDatabase.getInstance().getReference("");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        final EditText userName = (EditText) findViewById(R.id.userNameEditText);


        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameStr = userName.getText().toString();
                User u = new User();
                u.setName(userNameStr);
                database.child("users").child(userNameStr).child("points").setValue("");
                database.child("users").child(userNameStr).child("levels").setValue("");
                Intent intent = new Intent(LogIn.this, LevelsActivity.class);
                intent.putExtra("user", userNameStr);
                startActivity(intent);
            }
        });


    }
}
