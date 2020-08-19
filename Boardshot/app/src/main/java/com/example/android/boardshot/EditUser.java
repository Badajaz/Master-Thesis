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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditUser extends AppCompatActivity {


    private String user;
    private DatabaseReference database;
    private boolean existe;
    private String activityReturn;
    private HashMap<String, String> map;
    private String sequence;
    private String message;
    private int linha;
    private int coluna;
    private String levels;
    private int increment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        database = FirebaseDatabase.getInstance().getReference().child("Users");


        invalidateOptionsMenu();
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = bundle.getString("user");
        activityReturn = bundle.getString("activity");
        if (activityReturn.equals("BoardDraw")){

            map  = (HashMap<String, String>)intent.getSerializableExtra("map");;
            sequence = bundle.getString("sequence");
            message = bundle.getString("message");
            linha = bundle.getInt("roboLinha");
            coluna = bundle.getInt("roboColuna");
            levels = bundle.getString("levels");
            levels = bundle.getString("levels");
            increment = bundle.getInt("increment");
        }

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
                                ArrayList<Integer> level = g.getLevels();
                                ArrayList<Boolean> ChegouFim = g.getChegouFim();
                                userSnapshot.getRef().removeValue();
                                User u = new User(user,level,ChegouFim);
                                database.child(user).setValue(u);
                                Intent intentSend = null;
                                if (activityReturn.equals("LevelsActivity")){
                                    intentSend = new Intent(getApplicationContext(), LevelsActivity.class);
                                    intentSend.putExtra("user", user);
                                    startActivity(intentSend);
                                }

                                if (activityReturn.equals("MainActivity")){
                                    intentSend = new Intent(getApplicationContext(), MainActivity.class);
                                    intentSend.putExtra("user", user);
                                    startActivity(intentSend);
                                }

                                if (activityReturn.equals("BoardDraw")){
                                    Intent intent = new Intent(EditUser.this, BoardDraw.class);
                                    intent.putExtra("map", map);
                                    intent.putExtra("user", user);
                                    intent.putExtra("sequencia", sequence);
                                    intent.putExtra("message", message);
                                    intent.putExtra("roboLinha", linha);
                                    intent.putExtra("roboColuna", coluna);
                                    intent.putExtra("levels", levels);
                                    intent.putExtra("increment", increment);
                                    intent.putExtra("activity", "BoardDraw");
                                    startActivity(intent);
                                }



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
