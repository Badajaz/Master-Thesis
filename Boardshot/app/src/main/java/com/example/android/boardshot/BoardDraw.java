package com.example.android.boardshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
    private String orientation;
    private int currentLine;
    private int currentCollumn;
    private String levels;
    private String user;
    private int speechCount = 0;
    private  TimerTask taskTalk;
    private String message;
    private String sequencia;
    private int linha;
    private int coluna;
    private HashMap<String, String> hashMap;
    private DatabaseReference database;
    private ArrayList<Integer> pointsList;
    private ArrayList<Boolean> chegouFim;
    private int points;
    private int index;
    private int increment = 0;
    private String activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_draw);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Python.isStarted()){
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        if (Python.isStarted()){
            //Toast.makeText(getApplicationContext(),"Python Started",Toast.LENGTH_LONG).show();
        }


        invalidateOptionsMenu();
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);
        getSupportActionBar().setTitle("Descarregar Sequência");

        database = FirebaseDatabase.getInstance().getReference("").child("Users");


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        message = bundle.getString("message");
        hashMap = (HashMap<String, String>)intent.getSerializableExtra("map");
        sequencia = bundle.getString("sequencia");
        String[] messageArray = message.split(" ");
        linha = bundle.getInt("roboLinha");
        coluna = bundle.getInt("roboColuna");
        levels = bundle.getString("levels");
        user = bundle.getString("user");
        activity = bundle.getString("activity");
        if(activity.equals("BoardDraw")){
            increment = bundle.getInt("increment");
        }

        index = indexLevelPoints();
        //pointsList = (ArrayList<Integer>)intent.getIntegerArrayListExtra("pointsList");


        //Toast.makeText(getApplicationContext(),"Deu = "+pointsList.get(0)+"",Toast.LENGTH_LONG).show();

        textViewID = findViewById(R.id.TextViewID);
        textViewID.setOnTouchListener(this);
        gd = new GestureDetector(this,this);



        //ArrayList<String> comp =  computationBoard(hashMap,sequencia,linha,coluna);
        ArrayList<String> comp =  computationEgocentric(hashMap,sequencia,linha,coluna);
        writeInstructionsFile(comp);
        introSpeach();
        speakAudioFeedbackInstructions();



        Python py= Python.getInstance();
        PyObject initModule = py.getModule("Runnable");
        PyObject runCall= initModule.callAttr("test",Environment.getExternalStorageDirectory().toString()+"/Pictures/"+"test.ozopy");
        colorCode = runCall.toString();




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
                int loopIt = (it * getNumberInstructions(instructions,countLoop));
                int index = countLoop+1;
                countLoop+=2;


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
            //database.child(user).child("levels").setValue("100");
        }

        else if(board.get(linha+""+coluna).equals("X") ){
            feedbackAudios.add("bateu num obstáculo");
        }

        else if(instructions[countLoop].equals("F")){
            feedbackAudios.add("sequencia terminou numa casa possível");
        }




        return robotInstructions;
    }

    private ArrayList<String> computationEgocentric2(HashMap board,String sequence,int linha,int coluna){
        countLoop = 0;
        String[] instructions = sequence.split("_");
        ArrayList<String> robotInstructions = new ArrayList<>();
        int[] finalCoordenates = getGoalCoordenates(board);
        orientation = OrientedAvailableCoordenates(linha,coluna,board);
        //orientation = checkRobotStartOrientation(linha,coluna,finalCoordenates[0],finalCoordenates[1]);
        currentLine = linha;
        currentCollumn = coluna;



        while((!board.get(currentLine+""+currentCollumn).equals("F") && !board.get(currentLine+""+currentCollumn).equals("X"))
                && (!instructions[countLoop].equals("F"))){

            if(instructions[countLoop].equals("LB")){

                int it = Integer.parseInt(instructions[countLoop+1]);
                int loopIt = (it * getNumberInstructions(instructions,countLoop));
                int index = countLoop+1;
                int loopend = -1;
                countLoop+=2;

                for (int i = 0;i < loopIt && (!board.get(currentLine+""+currentCollumn).equals("F") && !board.get(currentLine+""+currentCollumn).equals("X"));i++){
                    //caso normal loop
                    String insaux = InstructionEgocentric(instructions[countLoop]);
                    robotInstructions.add(insaux);
                    getCurrentOrientation(instructions[countLoop]);
                    if (instructions[countLoop+1].equals("LE")){
                        loopend = countLoop+1;
                        countLoop = index+1;
                    }else{
                        countLoop++;
                    }


                }
                countLoop = loopend+1;

            }else{

                //caso normal sem loops
                String insaux = InstructionEgocentric(instructions[countLoop]);
                robotInstructions.add(insaux);
                getCurrentOrientation(instructions[countLoop]);
                countLoop++;

            }



        }

        if(board.get(currentLine+""+currentCollumn).equals("F") ){
            feedbackAudios.add("Chegou ao objectivo");
        }

        else if(board.get(currentLine+""+currentCollumn).equals("X") ){
            feedbackAudios.add("bateu num obstáculo");
        }

        else if(instructions[countLoop].equals("F")){
            feedbackAudios.add("sequencia terminou numa casa possível");
        }


        return  robotInstructions;
    }


    private ArrayList<String> computationEgocentric(HashMap board,String sequence,int linha,int coluna){

        countLoop = 0;
        String[] instructions = sequence.split("_");
        ArrayList<String> robotInstructions = new ArrayList<>();
        //int[] finalCoordenates = getGoalCoordenates(board);
        orientation = OrientedAvailableCoordenates(linha,coluna,board);
        //orientation = checkRobotStartOrientation(linha,coluna,finalCoordenates[0],finalCoordenates[1]);
        currentLine = linha;
        currentCollumn = coluna;



        while((!board.get(currentLine+""+currentCollumn).equals("F") && !board.get(currentLine+""+currentCollumn).equals("X"))
                && (!instructions[countLoop].equals("F"))){

            if(instructions[countLoop].equals("LB")){

                int it = Integer.parseInt(instructions[countLoop+1]);
                int loopIt = (it * getNumberInstructions(instructions,countLoop));
                int index = countLoop+1;
                int loopend = -1;
                countLoop+=2;



                for (int i = 0;i < loopIt && (!board.get(currentLine+""+currentCollumn).equals("F") && !board.get(currentLine+""+currentCollumn).equals("X"));i++){
                    //caso normal loop

                    if (instructions[countLoop].equals("LB")){
                        int insideIt = Integer.parseInt(instructions[countLoop+1]);
                        int insideloopIt = (insideIt * getNumberInstructions(instructions,countLoop));
                        int insideIndex = countLoop+1;
                        int insideloopend = -1;
                        countLoop+=2;
                        for (int j = 0;j < insideloopIt && (!board.get(currentLine+""+currentCollumn).equals("F") && !board.get(currentLine+""+currentCollumn).equals("X"));j++){

                            String insaux = InstructionEgocentric(instructions[countLoop]);
                            robotInstructions.add(insaux);
                            getCurrentOrientation(instructions[countLoop]);
                            if (instructions[countLoop+1].equals("LE")){
                                insideloopend = countLoop+1;
                                countLoop = index+1;
                            }else{
                                countLoop++;
                            }

                        }
                        countLoop = insideloopend+1;

                    }else{
                       if (!instructions[countLoop].equals("LE")) {
                           String insaux = InstructionEgocentric(instructions[countLoop]);
                           robotInstructions.add(insaux);
                           getCurrentOrientation(instructions[countLoop]);
                           if (instructions[countLoop + 1].equals("LE")) {
                               loopend = countLoop + 1;
                               countLoop = index + 1;
                           } else {
                               countLoop++;
                           }
                       }

                    }

                    }
                countLoop = loopend+1;




            }else{

                //caso normal sem loops
                String insaux = InstructionEgocentric(instructions[countLoop]);
                robotInstructions.add(insaux);
                getCurrentOrientation(instructions[countLoop]);
                countLoop++;

                }



            }

            if(board.get(currentLine+""+currentCollumn).equals("F") ) {
                feedbackAudios.add("Chegou ao objectivo");
                if(increment == 0){
                    increment++;
                    incrementLevel();


                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            final User g = userSnapshot.getValue(User.class);
                            if (g.getName().equals(user)) {
                                //ArrayList<Integer> pointLevelsAux = pointLevels;
                                //ArrayList<Boolean> ChegouFim = g.getChegouFim();
                                //int incrementPoints = pointLevels.get(indexLevelPoints())+100;
                                //pointLevelsAux.set(indexLevelPoints(),incrementPoints);

                                pointsList = g.getLevels();
                                chegouFim = g.getChegouFim();
                                points = pointsList.get(index) + 100;
                                pointsList.set(index, points);
                                chegouFim.set(index, true);
                                //Toast.makeText(getApplicationContext(), pointsList.get(index) + "", Toast.LENGTH_LONG).show();


                                g.setLevels(pointsList);
                                database.child(user).setValue(g);

                                //Toast.makeText(getApplicationContext(),pointsList.get(0)+"",Toast.LENGTH_LONG).show();
                                //Toast.makeText(getApplicationContext(),chegouFim.get(0)+"",Toast.LENGTH_LONG).show();


                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


                //ArrayList<Integer> points = pointsList;
                //int point = pointsList.get(indexLevelPoints())+100;
                //points.set(indexLevelPoints(),point);
                //database.child("Users").child(user).child("levels").setValue(point);

              
            }

            else if(board.get(currentLine+""+currentCollumn).equals("X") ){
                feedbackAudios.add("bateu num obstáculo");
            }

            else if(instructions[countLoop].equals("F")){
                feedbackAudios.add("sequencia terminou numa casa possível");

            }


            return  robotInstructions;
        }





        private void getCurrentOrientation(String move){

            if (orientation.equals("B")){

                if(move.equals("C")){
                    feedbackAudios.add("Vamos para a frente");
                    currentLine++;
                }else if(move.equals("D")){
                    feedbackAudios.add("Vamos virar para a direita");
                    orientation = "E";

                }else if(move.equals("E")){
                    feedbackAudios.add("Vamos virar para a esquerda");
                    orientation = "D";
                }



            }else if(orientation.equals("C")){
                if(move.equals("C")){
                    feedbackAudios.add("Vamos para a frente");
                    currentLine--;
                }else if(move.equals("D")){
                    feedbackAudios.add("Vamos virar para a direita");
                    orientation = "D";

                }else if(move.equals("E")){
                    feedbackAudios.add("Vamos virar para a esquerda");
                    orientation = "E";
                }


            }else if(orientation.equals("D")){

                if(move.equals("C")){
                    feedbackAudios.add("Vamos para a frente");
                    currentCollumn++;
                }else if(move.equals("D")){
                    feedbackAudios.add("Vamos virar para a direita");
                    orientation = "B";

                }else if(move.equals("E")){
                    feedbackAudios.add("Vamos virar para a esquerda");
                    orientation = "C";
                }


            }else if(orientation.equals("E")){

                if(move.equals("C")){
                    feedbackAudios.add("Vamos para a frente");
                    currentCollumn--;
                }else if(move.equals("D")){
                    feedbackAudios.add("Vamos virar para a direita");
                    orientation = "C";

                }else if(move.equals("E")){
                    feedbackAudios.add("Vamos virar para a esquerda");
                    orientation = "B";
                }
            }



        }












    private String checkRobotStartOrientation(int startLine,int startCollumn,int endLine,int endCollumn){

        String orientation = "";

        if(startLine == endLine){
            if (startCollumn < endCollumn){
                orientation = "D";
            }else  if (startCollumn > endCollumn){
                orientation = "E";
            }

        }

        if (startCollumn == endCollumn){
            if (startLine < endLine){
                orientation = "B";
            }else if (startLine > endLine){
                orientation = "C";
            }
        }

        if (startLine < endLine){




            orientation = "B";
        }


        if (startLine > endLine){
            orientation = "C";

        }



        return orientation;
    }


    private String OrientedAvailableCoordenates(int startLine,int startCollumn,HashMap<String,String> map){

        String dir = "";
        if (map.get(""+(startLine+1)+""+startCollumn).equals("O")  &&
                map.get(""+(startLine+1)+""+(startCollumn+1)).equals("X") &&
                        map.get(""+(startLine+1)+""+(startCollumn-1)).equals("X")){
            dir+="B";
        }

        if (map.get(""+(startLine-1)+""+startCollumn).equals("O") &&
                map.get(""+(startLine-1)+""+(startCollumn+1)).equals("X")&&
                map.get(""+(startLine-1)+""+(startCollumn-1)).equals("X")){
            dir+="C";
        }

        if (map.get(""+startLine+""+(startCollumn+1)).equals("O") &&
                map.get(""+(startLine-1)+""+(startCollumn+1)).equals("X") &&
                map.get(""+(startLine+1)+""+(startCollumn+1)).equals("X")){

            dir+="D";
        }
        if (map.get(""+startLine+""+(startCollumn-1)).equals("O") &&
                map.get(""+(startLine-1)+""+(startCollumn-1)).equals("X") &&
                map.get(""+(startLine+1)+""+(startCollumn-1)).equals("X")){

            dir+="E";
        }

        return dir;
    }


    private int[] getGoalCoordenates(HashMap board){
        String coordenates = "";
        Object[] keys = board.keySet().toArray();
        for( int i = 0;i< keys.length; i++){
            if (board.get(keys[i].toString()).equals("F")){
                coordenates = keys[i].toString();
                break;
            }
        }
        int[] returnCoordenates = {Integer.parseInt(coordenates.charAt(0)+""),Integer.parseInt(coordenates.charAt(1)+"")};


        return  returnCoordenates;
    }





    private int[] getCurrentPositionEgocentric(String movement,String previousMovement,int linha,int coluna){
        int l = linha;
        int c = coluna;
        if (movement.equals("D")){
            feedbackAudios.add("Vamos virar para a direita");

        } else if (movement.equals("E")){
            feedbackAudios.add("Vamos virar para a esquerda");

        }
       else if (movement.equals("C")){
            l--;
            feedbackAudios.add("Vamos para a frente");
            if (previousMovement.equals("D"))
                c++;
            if (previousMovement.equals("E"))
                c--;

        }else if (movement.equals("B")){
            l++;

        }



        int[] pos = {l,c};
        return pos;
    }

    private String  InstructionEgocentric(String currentMovement){

        switch (currentMovement){
            case "D":
                return "\t rotate(-90,50) \n";
            case "E":
                return "\t rotate(90,50) \n";
            case "C":
                return "\t move(30,30) \n";
            case "B":
                return "\t move(30,30) \n";

            default:
                return "";

        }



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
                break;
            }
        }

        return  index;
    }

private int getNumberInstructions(String[] sequence,int begining) {

    int numberInstructions= 0;
    int i = begining+2;

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
        //super.onBackPressed();
        Intent intent = new Intent(BoardDraw.this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("levels", levels);
        startActivity(intent);
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

    private void introSpeach() {


        engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                engine.setLanguage(new Locale("pt", "PT"));
            }
        });


        final Timer t = new Timer("Timer");
        taskTalk = new TimerTask() {
            public void run() {
                if (speechCount == 0){
                    engine.speak("JÁ TENHO AS INSTRUÇÔES! AGORA PRECISO DE COMUNICAR AO ROBÔ!",TextToSpeech.QUEUE_FLUSH,null,null);
                }else if (speechCount == 1) {
                    engine.speak("Carrega uma vez no robô até aparecer uma luz branca piscar", TextToSpeech.QUEUE_FLUSH, null, null);
                }else if (speechCount == 2) {
                    engine.speak("Agora volta a carregar e coloca o robô no tablet", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if(speechCount == 3){
                    engine.speak("Para passar as instruções ao robô carrega duas vezes no ecrã",TextToSpeech.QUEUE_FLUSH,null,null);
                }else if (speechCount == 4){
                    engine.speak("Coloca na casa de partida e carrega duas vezes no butão do robô ",TextToSpeech.QUEUE_FLUSH,null,null);
                }else if (speechCount == 5){
                    taskTalk.cancel();
                    t.cancel();
                }
                speechCount++;
            }
        };


        long delay = 0;
        long period = 6000;

        t.scheduleAtFixedRate(taskTalk, delay, period);


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
            Intent i = new Intent(BoardDraw.this, MainActivity.class);
            i.putExtra("user", user);
            i.putExtra("levels", levels);
            startActivity(i);
        }else if (id == R.id.action_item_one) {
            Intent intent = new Intent(BoardDraw.this, EditUser.class);
            intent.putExtra("map", hashMap);
            intent.putExtra("user", user);
            intent.putExtra("activity", "BoardDraw");
            intent.putExtra("sequence", sequencia);
            intent.putExtra("message", message);
            intent.putExtra("roboLinha", linha);
            intent.putExtra("roboColuna", coluna);
            intent.putExtra("levels", levels);
            intent.putExtra("increment", increment);
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

    private int indexLevelPoints(){
        int level = 0;
        if (levels.equals("level1")){
            level = 0;
        }
        else if (levels.equals("level2")){
            level = 1;
        }
        else if (levels.equals("level3")){
            level = 2;
        }
        else if (levels.equals("level4")){
            level = 3;
        }
        else if (levels.equals("level5")){
            level = 4;
        }
        else if (levels.equals("level6")){
            level = 5;
        }
        else if (levels.equals("level7")){
            level = 6;
        }
        else if (levels.equals("level8")){
            level = 7;
        }
        else if (levels.equals("level9")){
            level = 8;
        }
        else if (levels.equals("level10")){
            level = 9;
        }
        else if (levels.equals("level11")){
            level = 10;
        }
        else if (levels.equals("level12")){
            level = 11;
        }
    return level;

    }

    private void incrementLevel(){
        if (levels.equals("level1")){
            levels = "level2";
        }
        else if (levels.equals("level2")){
            levels = "level3";
        }
        else if (levels.equals("level3")){
            levels = "level4";
        }
        else if (levels.equals("level4")){
            levels = "level5";
        }
        else if (levels.equals("level5")){
            levels = "level6";
        }
        else if (levels.equals("level6")){
            levels = "level7";
        }
        else if (levels.equals("level7")){
            levels = "level8";
        }
        else if (levels.equals("level8")){
            levels = "level9";
        }
        else if (levels.equals("level9")){
            levels = "level10";
        }
        else if (levels.equals("level10")){
            levels = "level11";
        }
        else if (levels.equals("level11")){
            levels = "level12";
        }
        else if (levels.equals("freestyle")){
            levels = "freestyle";
        }
    }





}
