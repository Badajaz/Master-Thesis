package com.example.android.boardshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditUser extends AppCompatActivity {


    private String user;
    private DatabaseReference database;
    private boolean existe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        database = FirebaseDatabase.getInstance().getReference().child("Users");


        invalidateOptionsMenu();
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);


        Bundle intent = getIntent().getExtras();
        user = intent.getString("user");
        Toast.makeText(getApplicationContext(),"UserEdit = "+user,Toast.LENGTH_LONG).show();

        final TextView userText = (TextView)  findViewById(R.id.EditUserTextView);
        userText.setText(user);

        final ImageView pencil = (ImageView) findViewById(R.id.editUserImageView);

        final EditText userEditText = (EditText) findViewById(R.id.editTextUser);
        userEditText.setVisibility(View.INVISIBLE);

        final TextView tvOk = (TextView)  findViewById(R.id.textViewOK);
        tvOk.setVisibility(View.INVISIBLE);
        tvOk.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                final String userAux  = user;
                userEditText.setVisibility(View.INVISIBLE);
                tvOk.setVisibility(View.INVISIBLE);
                user = userEditText.getText().toString();
                userText.setText(user);
                pencil.setVisibility(View.VISIBLE);
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            User g = userSnapshot.getValue(User.class);
                            if (g.getName().equals(userAux)) {
                                String level = g.getLevels();
                                int points = g.getPoints();
                                userSnapshot.getRef().removeValue();
                                User u = new User(user,points,level);
                                database.child(user).setValue(u);
                                Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //Intent intent = new Intent(EditUser.this, LevelsActivity.class);
                //intent.putExtra("user", user);
                //startActivity(intent);

            }
        });

        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEditText.setVisibility(View.VISIBLE);
                pencil.setVisibility(View.INVISIBLE);
                tvOk.setVisibility(View.VISIBLE);
                userText.setText("");
                userEditText.setText(user);
            }
        });


    }

}
