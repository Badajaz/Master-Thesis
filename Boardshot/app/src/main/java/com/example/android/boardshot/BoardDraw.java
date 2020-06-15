package com.example.android.boardshot;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class BoardDraw extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_draw);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("message");
        String[] messageArray = message.split(" ");


        int left = 50; // initial start position of rectangles (50 pixels from left)
        int top = 50; // 50 pixels from the top
        int width = 80;
        int height = 80;
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


        Bitmap bit = Bitmap.createBitmap(5000, 5000, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bit);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));


        canvas.drawRect(left, top+1500  , left+4900, top+5000, paint);
        ImageView iV2 = new ImageView(this);

        iV2.setImageBitmap(bit);
        ll.addView(iV2);

        Python py = Python.getInstance();
        PyObject initModule = py.getModule("__init__.pyi");
        PyObject runCall= initModule.callAttr("run","app/src/main/python/ozopython/test.ozopy");


    }




}
