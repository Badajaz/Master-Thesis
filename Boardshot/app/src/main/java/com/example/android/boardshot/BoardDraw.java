package com.example.android.boardshot;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BoardDraw extends AppCompatActivity {

    private int left;
    private int top;
    private int width;
    private int height;

    private TextView textViewID;
    private String colorCode;
    private ValueAnimator colorAnimation = null;

    private int count = 1;
    private int countLoop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_draw);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String message = bundle.getString("message");
        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("map");
        String[] messageArray = message.split(" ");
        Toast.makeText(getApplicationContext(),hashMap.get("00"),Toast.LENGTH_LONG).show();

        textViewID = findViewById(R.id.TextViewID);


         left = 50; // initial start position of rectangles (50 pixels from left)
         top = 50; // 50 pixels from the top
         width = 80;
         height = 80;
        Bitmap bg = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        int index = 0;
        int col;
        for (int row = 0; row < 8; row++) { // draw 2 rows
            for(col = 0; col < 6; col++) { // draw 4 columns
                Paint paint = new Paint();
                if (messageArray[index].contains("O")){

                    Log.d("AA","O"+index);
                    paint.setColor(Color.parseColor("#008000"));

                }else if(messageArray[index].contains("X")){
                    Log.d("AA","X"+index);
                    paint.setColor(Color.parseColor("#CD5C5C"));

                }else if(messageArray[index].contains("F")) {
                    Log.d("AA","F"+index);
                    paint.setColor(Color.parseColor("#3792cb"));

                }else if(messageArray[index].contains("R")) {
                    Log.d("AA", "R"+index);
                    paint.setColor(Color.parseColor("#000000"));
                }


                Canvas canvas = new Canvas(bg);
                canvas.drawRect(left, top, left+width, top+height, paint);
                left = (left + width  +10); // set new left co-ordinate + 10 pixel gap
                // Do other things here
                // i.e. change colour
                index++;
            }

            top = top + height + 10; // move to new row by changing the top co-ordinate
            left = 50;
        }

        RelativeLayout ll = (RelativeLayout) findViewById(R.id.rect);
        ImageView iV = new ImageView(this);
        iV.setImageBitmap(bg);
        ll.addView(iV);
        




        try{
            File file = new File( Environment.getExternalStorageDirectory().toString()+"/Pictures/"+"test.ozopy");
            FileWriter writer = new FileWriter(file);
            writer.append("def f():\n");
            writer.append("\t move(30,30) \n");
            writer.append("\t rotate(90,30) \n");
            writer.append("\t move(30,30) \n");
            writer.append("\t wheels(0, 0) \n ");
            writer.append("\n");
            writer.append("i = 0 \n");
            writer.append("while i < 1: \n");
            writer.append("\t f()\n");
            writer.append("\t i = i + 1 ");
            writer.flush();
            writer.close();

            RandomAccessFile f = new RandomAccessFile(file, "r");
            byte[] b = new byte[(int)f.length()];
            f.readFully(b);
            OutputStream outputStream2 = null;
            try {
                outputStream2 = new FileOutputStream(file);
                outputStream2.write(b);


            } finally {
                if (outputStream2 != null)
                    outputStream2.close();
            }
            

        }catch (Exception e){
            e.printStackTrace();

        }

        Python py= Python.getInstance();
        PyObject initModule = py.getModule("Runnable");
        PyObject runCall= initModule.callAttr("test",Environment.getExternalStorageDirectory().toString()+"/Pictures/"+"test.ozopy");
        colorCode = runCall.toString();






        Button b = findViewById(R.id.loadRobot);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Timer timer = new Timer("Timer");
                //Toast.makeText(getApplicationContext(), "DEPOIS BUTTON = " + colorCode, Toast.LENGTH_LONG).show();
                Log.d("COLOR", colorCode);
                TimerTask repeatedTask = null;
                count = 0;
                if (count < colorCode.length()) {
                     repeatedTask = new TimerTask() {
                        public void run() {
                            if (count < colorCode.length()) {
                                int colorTo = getColor(colorCode.charAt(count));

                                GradientDrawable tvBackground2 = (GradientDrawable) textViewID.getBackground();
                                tvBackground2.setColor(colorTo);

                                //textView.setBackgroundColor(colorTo);
                                count += 1;
                            }
                        }


                    } ;
                }else{
                    repeatedTask.cancel();
                    timer.cancel();

                }


                long delay = 0;
                long period = 50;
                timer.scheduleAtFixedRate(repeatedTask, delay, period);



                }
               // colorAnimation.start();



             /*   for (int i = 1; i < colorCode.length(); i+=2){
                    Log.d("TEST",i+"");
                    int colorFrom = Color.parseColor(String.valueOf(colormap.get(colorCode.charAt(i-1))));
                    int colorTo = Color.parseColor(String.valueOf(colormap.get(colorCode.charAt(i))));
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(50); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            textView.setBackgroundColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();

                }*/




        });



    }
    private int getColor(char color){

        switch (color) {
            case 'K':
                return Color.parseColor("#000000");
            case 'R':
                return Color.parseColor("#ff0000");
            case 'G':
                return Color.parseColor("#00ff00");
            case 'Y':
                return  Color.parseColor("#ffff00");
            case 'B':
                return Color.parseColor("#0000ff");
            case 'M':
                return Color.parseColor("#ff00ff");
            case 'C':
                return  Color.parseColor("#00ffff");
            case 'W':
                return Color.parseColor("#ffffff");

        }
    return 0;
    }

    private ArrayList<String> computationBoard(HashMap board,String sequence){
        int linha = 0;
        int coluna = 0;
        countLoop = 0;

        String[] instructions = sequence.split("_");
        ArrayList<String> robotInstructions = new ArrayList<>();


        while((!board.get(linha+""+coluna).equals("F")) && (instructions[countLoop].equals("F"))){

            if(instructions[countLoop].equals("LB")){

                int it = Integer.parseInt(instructions[countLoop+1]);
                countLoop++;
                for (int i = 0;i < it && (!instructions[countLoop].equals("LE"));i++){

                    if (instructions[countLoop].equals("LB")){
                        robotInstructions.addAll(loopInstructions(instructions));
                    }else{
                        robotInstructions.add("\t move(30,30) \n");
                        robotInstructions.add(turnOverInstruction(instructions[countLoop],instructions[countLoop+1]));
                        countLoop++;
                    }

                }
                countLoop++;

            }else{
                robotInstructions.add("\t move(30,30) \n");
                robotInstructions.add(turnOverInstruction(instructions[countLoop],instructions[countLoop+1]));
                countLoop++;
            }


        }

        return null;
    }


    private String turnOverInstruction(String currentMovement,String nextMovement){

        String rotacao = "";

        if(currentMovement == "D") {

            if (nextMovement == "B") {
                rotacao = "\t rotate(-90,50) \n";
            } else if (nextMovement == "C") {
                rotacao = "\t rotate(90,50) \n";

            } else if (nextMovement == "E") {
                rotacao = "\t rotate(90,50) \n \t rotate(90,50) \n";

            }
        }


        else if(currentMovement == "E") {

                if (nextMovement == "B"){
                    rotacao = "\t rotate(90,50) \n";
                }
                else if(nextMovement =="C"){
                    rotacao = "\t rotate(-90,50) \n";

                }
                else if(nextMovement =="D"){
                    rotacao = "\t rotate(90,50) \n \t rotate(90,50) \n";


                }
        }

        else if (currentMovement == "C"){

            if (nextMovement == "B"){
                rotacao = "\t rotate(90,50) \n \t rotate(90,50) \n";
            }
            else if (nextMovement == "D"){
                rotacao = "\t rotate(-90,50) \n";
            }
            else if (nextMovement == "E"){
                rotacao = "\t rotate(90,50) \n";
            }

        }


        else if (currentMovement == "B"){

            if (nextMovement == "C"){
                rotacao = "\t rotate(90,50) \n \t rotate(90,50) \n";
            }
            else if (nextMovement == "D"){
                rotacao = "\t rotate(90,50) \n";

            }
            else if (nextMovement == "E"){
                rotacao = "\t rotate(-90,50) \n";
            }

        }

        return rotacao;
    }

    private  ArrayList<String> loopInstructions(String[] arrayInstructions){

        ArrayList<String> robotInstructions = new ArrayList<>();
        int it = Integer.parseInt(arrayInstructions[countLoop+1]);
        countLoop++;
        for (int i = 0;i < it && (!arrayInstructions[countLoop].equals("LE"));i++){
            robotInstructions.add("\t move(30,30) \n");
            robotInstructions.add(turnOverInstruction(arrayInstructions[countLoop],arrayInstructions[countLoop+1]));
            countLoop++;
        }
        countLoop++;


        return  robotInstructions;
    }




}
