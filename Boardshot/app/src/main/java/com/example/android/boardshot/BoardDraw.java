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
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BoardDraw extends AppCompatActivity implements View.OnTouchListener,GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    private int left;
    private int top;
    private int width;
    private int height;

    private TextView textViewID;
    private String colorCode;

    private int count = 1;
    private int countLoop;

    private ArrayList<String> feedbackAudios = new ArrayList<>();
    TextToSpeech engine = null;

    private int countIns = 0;
    private GestureDetector gd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_draw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String message = bundle.getString("message");
        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("map");
        String sequencia = bundle.getString("sequencia");
        String[] messageArray = message.split(" ");

        //Toast.makeText(getApplicationContext(),sequencia,Toast.LENGTH_LONG).show();

        textViewID = findViewById(R.id.TextViewID);
        textViewID.setOnTouchListener(this);
        gd = new GestureDetector(this,this);


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



        int linha = 5;
        int coluna = 2;
        String finale = hashMap.get("34");
        ArrayList<String> comp =  computationBoard(hashMap,sequencia,linha,coluna);
        writeInstructionsFile(comp);

        speakAudioFeedbackInstructions();





        Python py= Python.getInstance();
        PyObject initModule = py.getModule("Runnable");
        PyObject runCall= initModule.callAttr("test",Environment.getExternalStorageDirectory().toString()+"/Pictures/"+"test.ozopy");
        colorCode = runCall.toString();





        /*Button b = findViewById(R.id.loadRobot);

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

                }




        });*/



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

    /**
     *
     * ficam a faltar  os innerLoops TODO
     *
     *
     *
     * **/
    private ArrayList<String> computationBoard(HashMap board,String sequence,int linha,int coluna){
        countLoop = 0;
        int count = 0;
        int countIns= 0 ;
        int indexLE = 0;

        String[] instructions = sequence.split("_");
        ArrayList<String> robotInstructions = new ArrayList<>();


        while((!board.get(linha+""+coluna).equals("F") && !board.get(linha+""+coluna).equals("X")) && (!instructions[countLoop].equals("F"))){

            if(instructions[countLoop].equals("LB")){

                int it = Integer.parseInt(instructions[countLoop+1]);
                int index = countLoop+1;
                countLoop+=2;
                int loopIt = (it * getNumberInstructions(instructions));

                for (int i = 0;i < loopIt && (!board.get(linha+""+coluna).equals("F") && !board.get(linha+""+coluna).equals("X"));i++){

                    if (instructions[countLoop].equals("LB")){
                        robotInstructions.addAll(loopInstructions(instructions)); // Verificar metodo loopInstructions
                    }else{
                        robotInstructions.add("\t move(30,30) \n");
                        if (instructions[countLoop+1].equals("LE")){
                            indexLE = countLoop+1;
                            int beforeCountLoop = countLoop;
                            countLoop = index+1;
                            if (i != loopIt - 1)
                                robotInstructions.add(turnOverInstruction(instructions[beforeCountLoop],instructions[countLoop]));
                            
                            countLoop--;

                        }else{
                            countIns++;
                            robotInstructions.add(turnOverInstruction(instructions[countLoop],instructions[countLoop+1]));

                        }
                        int[] pos = getCurrentPosition(instructions[countLoop],linha,coluna);
                        linha = pos[0];
                        coluna = pos[1];
                       countLoop++;
                    }
                }

                countLoop = indexLE + 1;

            }else{
                if (countLoop > 0) {
                    if (instructions[countLoop - 1].equals("LE")) {
                        robotInstructions.add(turnOverInstruction(instructions[countLoop - 2], instructions[countLoop]));
                    }
                }

                robotInstructions.add("\t move(30,30) \n");
                if (instructions[countLoop+1].equals("LB")){
                    robotInstructions.add(turnOverInstruction(instructions[countLoop],instructions[countLoop+3]));

                }else{
                    robotInstructions.add(turnOverInstruction(instructions[countLoop],instructions[countLoop+1]));
                }
                int[] pos = getCurrentPosition(instructions[countLoop],linha,coluna);
                linha = pos[0];
                coluna = pos[1];
                countLoop++;
            }


        }
        if(board.get(linha+""+coluna).equals("F") ){
            feedbackAudios.add("Chegou ao objectivo");
        }

        else if(board.get(linha+""+coluna).equals("X") ){
            feedbackAudios.add("bateu num obstáculo");
        }

        else if(instructions[countLoop].equals("F")){
            feedbackAudios.add("sequencia terminou numa casa possível");
        }




        return robotInstructions;
    }


    private String turnOverInstruction(String currentMovement,String nextMovement){

        String rotacao = "";

        if(currentMovement.equals("D")) {

            if (nextMovement.equals("B")) {
                rotacao = "\t rotate(-90,50) \n";

            } else if (nextMovement.equals("C")) {
                rotacao = "\t rotate(90,50) \n";

            } else if (nextMovement.equals("E")) {
                rotacao = "\t rotate(90,50) \n  \t rotate(90,50) \n";

            }
        }


        else if(currentMovement.equals("E")) {

                if (nextMovement.equals("B")){
                    rotacao = "\t rotate(90,50) \n";
                }
                else if(nextMovement.equals("C")){
                    rotacao = "\t rotate(-90,50) \n";

                }
                else if(nextMovement.equals("D")){
                    rotacao = "\t rotate(90,50) \n  \t rotate(90,50) \n";


                }
        }

        else if (currentMovement.equals("C")){

            if (nextMovement.equals("B")){
                rotacao = "\t rotate(90,50) \n  \t rotate(90,50) \n";
            }
            else if (nextMovement.equals("D")){
                rotacao = "\t rotate(-90,50) \n";
            }
            else if (nextMovement.equals("E")){
                rotacao = "\t rotate(90,50) \n";
            }

        }


        else if (currentMovement.equals("B")){

            if (nextMovement.equals("C")){
                rotacao = "\t rotate(90,50) \n  \t rotate(90,50) \n";
            }
            else if (nextMovement.equals("D")){
                rotacao = "\t rotate(90,50) \n";

            }
            else if (nextMovement.equals("E")){
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


    private int[] getCurrentPosition(String movement,int linha,int coluna){
        int l = linha;
        int c = coluna;

        if (movement.equals("D")){
            c++;
            feedbackAudios.add("Vamos para a direita");
        }
        else if (movement.equals("E")){
            c--;
            feedbackAudios.add("Vamos para a esquerda");
        }
        else if (movement.equals("C")){
            l--;
            feedbackAudios.add("Vamos para cima");
        }
        else if (movement.equals("B")){
            l++;
            feedbackAudios.add("Vamos para baixo");
        }
        int[] pos = {l,c};
        return pos;
    }

    private void writeInstructionsFile(ArrayList<String> instructions){


        try{
            File file = new File( Environment.getExternalStorageDirectory().toString()+"/Pictures/"+"test.ozopy");
            FileWriter writer = new FileWriter(file);
            writer.append("def f():\n");
            for (String ins: instructions){
                if (ins.contains("  ")){
                   String[] array = ins.split("  ");
                    writer.append(array[0]);
                    writer.append(array[1]);
                }else{
                    writer.append(ins);

                }
            }
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


    }



private int getNumberCicles(String[] sequence){
        int numberCicles = 0;

        for (String s : sequence){

            if(s.equals("LB")){
                numberCicles++;
            }
        }


    return numberCicles;
}

    private int getIndexBeginCicle(String[] sequence){
        int index = 0;
        for (int i = 0;i < sequence.length;i++){

            if(sequence[i].equals("LB")){
                index = i;
            }
        }

        return  index;
    }

private int getNumberInstructions(String[] sequence) {
    int numberInstructions= 0;
    int i = getIndexBeginCicle(sequence)+2;

    while(!sequence[i].equals("LE")){
        numberInstructions++;

        i++;
    }



return  numberInstructions;
}


private void speakAudioFeedbackInstructions(){

    engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            engine.setLanguage(new Locale("pt", "PT"));
        }
    });


    Timer t = new Timer("Timer");

        TimerTask task = new TimerTask() {
        public void run() {
            if (countIns < feedbackAudios.size()) {
                engine.speak(feedbackAudios.get(countIns), TextToSpeech.QUEUE_FLUSH, null, null);
                countIns += 1;
            }
        }


    };

    long delay = 25000;
    long period = 2000;

    t.scheduleAtFixedRate(task, delay, period);



}


    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
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
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gd.onTouchEvent(motionEvent);
        return true;
    }
}
