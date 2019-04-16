package com.writedown.writedown;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;
import com.writedown.writedown.BaiduTranslate.TransApi;
import com.writedown.writedown.BaiduTranslate.TranslateResult;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class WritingTable extends AppCompatActivity {
    private ImageView iv;
    private Bitmap baseBitmap;
    private Button btn_resume;
    private Button btn_save;
    private Button btn_undo;
    private Canvas canvas;
    TextView result;
    private Paint paint;
    private ArrayList<Path> lines = new ArrayList();
    private Path singlePath = new Path();
    float radio;
    Handler handler = new Handler();
    String filename = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_table);
        radio = 5;
        result = findViewById(R.id.result);
        iv = (ImageView) findViewById(R.id.iv);
        paint = new Paint();
        paint.setStrokeWidth(radio);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        filename = getSharedPreferences("username", MODE_PRIVATE).getString("username", "unknown")+'-'+System.currentTimeMillis()+".jpg";
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
                    for (int i = 0; i < lines.size(); i++)
                        canvas.drawPath(lines.get(i), paint);
                    canvas.drawPath(singlePath, paint);
                    // 更新开始点的位置
                    preX = startX = event.getX();
                    preY = startY = event.getY();
                    // 把图片展示到ImageView中
                    iv.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP:

                    tryToTranslate(baseBitmap);

                    //这里直接用bitmap触发识别，不需要保存
                    //saveBitmap();
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
                    //saveBitmap();
                    tryToTranslate(baseBitmap);
                    break;
                default:
                    break;
            }
        }
    };

    protected void tryToTranslate(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational())
                    Log.e("Error", "not good");
                else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringbuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); ++i) {
                        TextBlock item = items.valueAt(i);
                        stringbuilder.append((item.getValue()));
                        stringbuilder.append("");
                    }

                    final String translate_text = stringbuilder.toString();
                    Log.e("Error", translate_text);

                    String resultJson = new TransApi().getTransResult(translate_text, "auto", "zh"); // query改动 翻译用
                    //get result and analyze the result
                    Log.i("tagg", resultJson);
                    Gson gson = new Gson();
                    TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
                    final List<TranslateResult.TransResultBean> trans_result = translateResult.getTrans_result();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(trans_result!=null) {
                                String dst = "";
                                for (TranslateResult.TransResultBean s : trans_result
                                ) {
                                    dst = dst + s.getDst()+"\n";
                                }
                                Log.i("tagg", dst);
                                result.setText(dst);
                            }
                        }
                    });

                }
            }
        }).start();
        }
    /**
     * 保存图片到SD卡上
     */

    protected void saveBitmap() {
        try {
            // 保存图片到SD卡上

            result.destroyDrawingCache();
            result.setDrawingCacheEnabled(true);
            Bitmap bp = result.getDrawingCache();
            Bitmap cache = Bitmap.createBitmap(bp);//复制bp
            result.setDrawingCacheEnabled(false);//销毁bp

            Bitmap b2 = newBitmap(500, baseBitmap, cache);
            File dir = new File("/sdcard/writedown");
            if(!dir.exists()) dir.mkdirs();
            String fileName = dir.getAbsolutePath()+"/"+filename;
            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            b2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Toast.makeText(WritingTable.this, "保存图片成功", Toast.LENGTH_SHORT).show();
               // Android设备Gallery应用只会在启动的时候扫描系统文件夹
               // 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看
            //tryToTranslate(baseBitmap);
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
            Toast.makeText(WritingTable.this, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public static Bitmap newBitmap(int width, Bitmap bit1, Bitmap bit2) {
        if (width <= 0) {
            return null;
        }

        int h1 =  bit1.getHeight() * width / bit1.getWidth();
        int h2 = bit2.getHeight() * width / bit2.getWidth();
        int height = h1 + h2; //缩放到屏幕宽度时候 合成后的总高度
        //创建一个空的Bitmap(内存区域),宽度等于第一张图片的宽度，高度等于两张图片高度总和
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Log.i("tagg", h1+","+h2);
        //缩放到指定大小的新bitmap
        Bitmap newSizeBitmap1 = getNewSizeBitmap(bit1, width, h1);
        Bitmap newSizeBitmap2 = getNewSizeBitmap(bit2, width, h2);

        //将bitmap放置到绘制区域,并将要拼接的图片绘制到指定内存区域
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(newSizeBitmap1, 0, 0, null);
        canvas.drawBitmap(newSizeBitmap2, 0, h1, null);
        return bitmap;
    }

    public static Bitmap getNewSizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap bit1Scale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
        return bit1Scale;
    }

    // 手动清除画板的绘图，重新创建一个画板
    protected void resumeCanvas() {
        if (baseBitmap != null) {
            baseBitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            lines.clear();
            iv.setImageBitmap(baseBitmap);
            Toast.makeText(WritingTable.this, "清除画板成功，可以重新开始绘图", Toast.LENGTH_SHORT).show();
        }
    }

    protected void undo() {
        // 移除最新的一条笔画，然后用和touch中一样的方式重绘
        lines.remove(lines.size() - 1);
        canvas.drawPoint(0, 0, paint);
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < lines.size(); i++)
            canvas.drawPath(lines.get(i), paint);
        iv.setImageBitmap(baseBitmap);
        if(lines.size() == 0) btn_undo.setEnabled(false);
    }
    }
