package com.example.test;

import android.graphics.Path;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.newdegree.draw.R;


import java.io.File;

import java.io.FileOutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private Bitmap baseBitmap;
    private Button btn_resume;
    private Button btn_undo;
    private Button btn_save;
    private Canvas canvas;
    private Paint paint;

    private ArrayList<Path> lines = new ArrayList();
    private Path singlePath = new Path();

    float radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radio = 5;
        iv = (ImageView) findViewById(R.id.iv);
        paint = new Paint();
        paint.setStrokeWidth(radio);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        iv = (ImageView) findViewById(R.id.iv);
        btn_resume = (Button) findViewById(R.id.btn_resume);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_undo = (Button) findViewById(R.id.btn_undo);

        btn_resume.setOnClickListener(click);
        btn_save.setOnClickListener(click);
        btn_undo.setOnClickListener(click);
        iv.setOnTouchListener(touch);
    }

    private View.OnTouchListener touch = new View.OnTouchListener() {
        // 定义手指开始触摸的坐标
        float startX;
        float startY;
        float preX;
        float preY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                // 用户按下动作
                case MotionEvent.ACTION_DOWN:
                    // 第一次绘图初始化内存图片，指定背景为白色
                    if (baseBitmap == null) {
                        baseBitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(Color.WHITE);
                    }
                    // 记录开始触摸的点的坐标
                    preX = startX = event.getX();
                    preY = startY = event.getY();

                    singlePath.moveTo(startX, startY);
                    break;
                // 用户手指在屏幕上移动的动作
                case MotionEvent.ACTION_MOVE:
                    // 记录移动位置的点的坐标
                    float stopX = event.getX();
                    float stopY = event.getY();

                    paint.setStrokeWidth(radio);

                    // 使用二阶贝塞尔曲线连接前后的点，添加到path中，这样的目的是让快速移动的时候的直线更圆滑
                    // 关于二阶贝塞尔曲线的作用和原理可以上网查
                    singlePath.quadTo(preX, preY, stopX, stopY);


                    //根据两点坐标，绘制连线
                    //canvas.drawLine(startX, startY, stopX, stopY, paint);

                    // 这里drawLine改成了每次重新填充白色，然后绘制list里所有path，再绘制正在处理的singlepath
                    canvas.drawColor(Color.WHITE);

                    for(int i=0; i<lines.size(); i++)
                        canvas.drawPath(lines.get(i), paint);

                    canvas.drawPath(singlePath, paint);
                    // 更新开始点的位置
                    preX = startX = event.getX();
                    preY = startY = event.getY();
                    // 把图片展示到ImageView中
                    iv.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP:
                    radio = 5;

                    // 把singlePath 另存到lines里面，相当于将其保存下来，然后reset等待下一次绘制
                    lines.add(new Path(singlePath));
                    // 当有笔画的时候将按钮设置为可点击
                    if(lines.size()>0) {
                        btn_undo.setEnabled(true);
                    }
                    singlePath.reset();

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
                case R.id.btn_undo:
                    undo();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 保存图片到SD卡上
     */
    protected void saveBitmap() {
        try {
            // 保存图片到SD卡上
            String fileName = "/sdcard/"+"1.png";
            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Toast.makeText(MainActivity.this, "保存图片成功", Toast.LENGTH_SHORT).show();
//             // Android设备Gallery应用只会在启动的时候扫描系统文件夹
//             // 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //扫描指定的文件,refresh the gallery
                final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                final Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            } else {
                final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(Environment.getExternalStorageDirectory()));
                sendBroadcast(intent);
            }

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // 手动清除画板的绘图，重新创建一个画板
    protected void resumeCanvas() {
        if (baseBitmap != null) {
            baseBitmap = Bitmap.createBitmap(iv.getWidth(),
                    iv.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            lines.clear();
            iv.setImageBitmap(baseBitmap);
            Toast.makeText(MainActivity.this, "清除画板成功，可以重新开始绘图", Toast.LENGTH_SHORT).show();
        }
    }

    protected void undo() {

        // 移除最新的一条笔画，然后用和touch中一样的方式重绘
        lines.remove(lines.size()-1);
        canvas.drawPoint(0,0, paint);

        canvas.drawColor(Color.WHITE);

        for(int i=0; i<lines.size(); i++)
            canvas.drawPath(lines.get(i), paint);
        iv.setImageBitmap(baseBitmap);


        }
    }
}

