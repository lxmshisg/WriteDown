package com.writedown.writedown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.writedown.writedown.dbHelper.COL_1;

public class WriteActivity extends AppCompatActivity {

    private ImageView iv;
    private Bitmap baseBitmap;
    private Button btn_resume;
    private Button btn_back;
    private Button btn_save;
    private Canvas canvas;
    private Paint paint;
    dbHelper db;
    private dbHelper dbHelper;
    float radio;
    EditText filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new dbHelper(this);
        setContentView(R.layout.activity_write);
        radio = 5;

        iv = (ImageView) findViewById(R.id.iv);
        // 初始化一个画笔，笔触宽度为5，颜色为红色
        paint = new Paint();
        paint.setStrokeWidth(radio);
        paint.setColor(Color.BLACK);
        iv = (ImageView) findViewById(R.id.iv);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        btn_resume = (Button) findViewById(R.id.btn_resume);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_back = (Button) findViewById(R.id.btn_back);

        iv.setOnTouchListener(touch);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder.setMessage("Save the Note?");
                alertDialogBuilder.setPositiveButton("Save",click1);
                alertDialogBuilder.setNegativeButton("Cancel",click2);
                AlertDialog alertdialog1 = alertDialogBuilder.create();

                alertdialog1.show();

            }private DialogInterface.OnClickListener click1=new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface arg0,int arg1)
                {
                    String filename = "test1";
                    dbHelper.addData(filename);

                }
            };
            private DialogInterface.OnClickListener click2=new DialogInterface.OnClickListener() {
                @Override

                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.cancel();
                }

            };


        });
        btn_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseBitmap != null) {
                    baseBitmap = Bitmap.createBitmap(iv.getWidth(),
                            iv.getHeight(), Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(baseBitmap);
                    canvas.drawColor(Color.WHITE);
                    iv.setImageBitmap(baseBitmap);
                    Toast.makeText(WriteActivity.this, "redraw ", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMain = new Intent(WriteActivity.this, MainActivity.class);
                startActivity(moveToMain);
            }
        });

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

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            radio += 0.1;

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t.start();

                    paint.setStrokeWidth(radio);
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
//transform any blob to the binary source to the sqlite
    public byte[] img(int id)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(id)).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
