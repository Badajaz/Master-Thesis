package com.example.android.boardshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LogIn extends AppCompatActivity {

    private DatabaseReference database;
    private User u;
    private boolean exists = false;
    private ValueEventListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Button btnLogIn = (Button) findViewById(R.id.buttonLogIn);
        database = FirebaseDatabase.getInstance().getReference().child("Users");
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);
        getSupportActionBar().setTitle("Log In");




        btnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final EditText userName = (EditText) findViewById(R.id.userNameEditText);
                final String userNameStr = userName.getText().toString();
                if(userNameStr.equals("") || userNameStr.equals(" ")){
                    Toast.makeText(getApplicationContext(),"Têm de inserir algum utilizador",Toast.LENGTH_LONG).show();
                }else{
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0){
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                                    User g = userSnapshot.getValue(User.class);
                                    if (g.getName().equals(userNameStr)) {
                                        exists = true;
                                        Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
                                        intent.putExtra("user", userNameStr);
                                        startActivity(intent);

                                    }
                                }
                                if (exists == false) {
                                    ArrayList<Integer> points =  new ArrayList<>();
                                    ArrayList<Boolean> chegouFim =  new ArrayList<>();
                                    User u = new User(userNameStr,points,chegouFim);
                                    database.child(userNameStr).setValue(u);
                                    Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
                                    intent.putExtra("user", userNameStr);
                                    startActivity(intent);
                                }


                            }else {
                                ArrayList<Integer> points = new ArrayList<>();
                                ArrayList<Boolean> chegouFim =  new ArrayList<>();
                                User u = new User(userNameStr,points,chegouFim);
                                database.child(userNameStr).setValue(u);
                                Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
                                intent.putExtra("user", userNameStr);
                                startActivity(intent);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


                userName.setText("");
            }


        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //database.removeEventListener(mListener);

    }

}
