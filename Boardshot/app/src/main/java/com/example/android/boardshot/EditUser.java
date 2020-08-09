package com.example.android.boardshot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditUser extends AppCompatActivity {


    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        invalidateOptionsMenu();
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        final TextView userText = (TextView)  findViewById(R.id.EditUserTextView);
        userText.setText(user);

        ImageView pencil = (ImageView) findViewById(R.id.editUserImageView);

        final EditText userEditText = (EditText) findViewById(R.id.editTextUser);
        userEditText.setVisibility(View.INVISIBLE);

        final TextView tvOk = (TextView)  findViewById(R.id.textViewOK);
        tvOk.setVisibility(View.INVISIBLE);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEditText.setVisibility(View.INVISIBLE);
                tvOk.setVisibility(View.INVISIBLE);
                user = userEditText.getText().toString();
                userText.setText(user);
            }
        });

        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEditText.setVisibility(View.VISIBLE);
                tvOk.setVisibility(View.VISIBLE);
                userText.setText("");
                userEditText.setText(user);
            }
        });





    }
}
