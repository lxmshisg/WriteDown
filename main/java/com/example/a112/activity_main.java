package com.example.a112;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class activity_main extends AppCompatActivity {
    private ImageView iv;
    private Bitmap baseBitmap;
    private Button btn_resume;
    private Button btn_save;
    private Canvas canvas;
    private Paint paint;

    float radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radio = 5;
        iv = findViewById(R.id.iv);

        paint = new Paint();
        paint.setStrokeWidth(radio);
        paint.setColor(Color.BLACK);
        iv =  findViewById(R.id.iv);
        btn_resume =  findViewById(R.id.btn_resume);
        btn_save = findViewById(R.id.btn_save);

        btn_resume.setOnClickListener(click);
        btn_save.setOnClickListener(click);
        iv.setOnTouchListener(touch);
    }

    private View.OnTouchListener touch = new View.OnTouchListener() {

        float startX;
        float startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    if (baseBitmap == null) {
                        baseBitmap = Bitmap.createBitmap(iv.getWidth(),
                                iv.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(Color.WHITE);
                    }

                    startX = event.getX();
                    startY = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:

                    float stopX = event.getX();
                    float stopY = event.getY();

                    canvas.drawLine(startX, startY, stopX, stopY, paint);


                    startX = event.getX();
                    startY = event.getY();

                    iv.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP:
                    radio = 5;
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save:
                    saveBitmap();
                    break;
                case R.id.btn_resume:
                    resumeCanvas();
                    break;
                default:
                    break;
            }
        }
    };


    protected void saveBitmap() {
        try {

            String fileName = "/sdcard/"+System.currentTimeMillis() + ".png";
            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Toast.makeText(activity_main.this, "save successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(Environment
                    .getExternalStorageDirectory()));
            sendBroadcast(intent);
        } catch (Exception e) {
            Toast.makeText(activity_main.this, "save failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    protected void resumeCanvas() {
        if (baseBitmap != null) {
            baseBitmap = Bitmap.createBitmap(iv.getWidth(),
                    iv.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            iv.setImageBitmap(baseBitmap);
            Toast.makeText(activity_main.this, "redraw ", Toast.LENGTH_SHORT).show();
        }
    }
}
