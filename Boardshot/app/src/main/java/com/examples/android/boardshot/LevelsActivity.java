package com.examples.android.boardshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LevelsActivity extends AppCompatActivity {

    private String user;
    private DatabaseReference database;
    private Button btnLevel1;
    private Button btnLevel2;
    private Button btnLevel3;
    private Button btnLevel4;
    private Button btnLevel5;
    private Button btnLevel6;
    private Button btnLevel7;
    private Button btnLevel8;
    private Button btnLevel9;
    private Button btnLevel10;
    private Button btnLevel11;
    private Button btnLevel12;
    private Button freestylebtn;
    private ArrayList<Integer> pointsLevels;
    private ArrayList<Boolean> chegouFim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        btnLevel1 = (Button) findViewById(R.id.level1);
        btnLevel2 = (Button) findViewById(R.id.level2);
        btnLevel3 = (Button) findViewById(R.id.level3);
        btnLevel4 = (Button) findViewById(R.id.level4);
        btnLevel5 = (Button) findViewById(R.id.level5);
        btnLevel6 = (Button) findViewById(R.id.level6);
        btnLevel7 = (Button) findViewById(R.id.level7);
        btnLevel8 = (Button) findViewById(R.id.level8);
        btnLevel9 = (Button) findViewById(R.id.level9);
        btnLevel10 = (Button) findViewById(R.id.level10);
        btnLevel11 = (Button) findViewById(R.id.level11);
        btnLevel12 = (Button) findViewById(R.id.level12);
        freestylebtn = (Button) findViewById(R.id.freestyle);
        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");
        invalidateOptionsMenu();
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);
        getSupportActionBar().setTitle("Níveis");
        database = FirebaseDatabase.getInstance().getReference().child("Users");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                        User g = userSnapshot.getValue(User.class);
                        if (g.getName().equals(user)) {
                            pointsLevels = g.getLevels();
                            chegouFim = g.getChegouFim();

                            btnLevel1.setText(btnLevel1.getText()+"\n Recorde: "+pointsLevels.get(0));
                            btnLevel2.setText(btnLevel2.getText()+"\n Recorde: "+pointsLevels.get(1));
                            btnLevel3.setText(btnLevel3.getText()+"\n Recorde: "+pointsLevels.get(2));
                            btnLevel4.setText(btnLevel4.getText()+"\n Recorde: "+pointsLevels.get(3));
                            btnLevel5.setText(btnLevel5.getText()+"\n Recorde: "+pointsLevels.get(4));
                            btnLevel6.setText(btnLevel6.getText()+"\n Recorde: "+pointsLevels.get(5));
                            btnLevel7.setText(btnLevel7.getText()+"\n Recorde: "+pointsLevels.get(6));
                            btnLevel8.setText(btnLevel8.getText()+"\n Recorde: "+pointsLevels.get(7));
                            btnLevel9.setText(btnLevel9.getText()+"\n Recorde: "+pointsLevels.get(8));
                            btnLevel10.setText(btnLevel10.getText()+"\n Recorde: "+pointsLevels.get(9));
                            btnLevel11.setText(btnLevel11.getText()+"\n Recorde: "+pointsLevels.get(10));
                            btnLevel12.setText(btnLevel12.getText()+"\n Recorde: "+pointsLevels.get(11));

                            if (chegouFim.get(0) == false){
                                btnLevel1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(1) == false){
                                btnLevel2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(2) == false){
                                btnLevel3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(3) == false){
                                btnLevel4.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel4.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(4) == false){
                                btnLevel5.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel5.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(5) == false){
                                btnLevel6.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel6.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }
                            if (chegouFim.get(6) == false){
                                btnLevel7.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel7.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(7) == false){
                                btnLevel8.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel8.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }
                            if (chegouFim.get(8) == false){
                                btnLevel9.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel9.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(9) == false){
                                btnLevel10.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel10.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }
                            if (chegouFim.get(10) == false){
                                btnLevel11.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel11.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }

                            if (chegouFim.get(11) == false){
                                btnLevel12.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.emptystar);
                            }else{
                                btnLevel12.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fullstar);
                            }






                        }
                    }

                }

                btnLevel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level1");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                btnLevel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level2");
                        //bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                btnLevel3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level3");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                btnLevel4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level4");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                btnLevel5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level5");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                btnLevel6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level6");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });


                btnLevel7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level7");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });



                btnLevel8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level8");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                btnLevel9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level9");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                btnLevel10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level10");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });


                btnLevel11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level11");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });


                btnLevel12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "level12");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                freestylebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
                        bundle.putString("user", user);
                        bundle.putString("levels", "freestyle");
                        bundle.putIntegerArrayList("pointsList",pointsLevels);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        if ( id ==  android.R.id.home){
            Intent i = new Intent(LevelsActivity.this, LogIn.class);
            startActivity(i);
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_one) {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(LevelsActivity.this, EditUser.class);
            bundle.putString("user", user);
            bundle.putString("activity", "LevelsActivity");
            intent.putExtras(bundle);
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
