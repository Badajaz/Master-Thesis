package com.example.android.boardshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LogIn extends AppCompatActivity {

    private DatabaseReference database;
    private User u = new User();
    private long userLength;

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
                final String userNameStr = userName.getText().toString();
                if(userNameStr.equals("") || userNameStr.equals(" ")){
                    Toast.makeText(getApplicationContext(),"Têm de inserir algum utilizador",Toast.LENGTH_LONG).show();
                }else{

                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean exists = false;
                            for(DataSnapshot data: dataSnapshot.getChildren()){

                                if (data.getKey().equals(userNameStr)) {
                                    exists = true;
                                }


                            }

                            if(exists){
                                Intent intent = new Intent(LogIn.this, LevelsActivity.class);
                                intent.putExtra("user", userNameStr);
                                intent.putExtra("contador", 0);
                                startActivity(intent);
                            }

                            else{
                                u.setName(userNameStr);
                                u.setLevels("");
                                database.child(userNameStr).setValue(u);
                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }



            }
        });


    }
}
