
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
import com.writedown.writedown.BaiduTranslate.TranslationAPI;
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
        //Defines the coordinates that finger begins to touch
        float startX;
        float startY;
        float preX;
        float preY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    //The first drawing initializes the memory image
                    if (baseBitmap == null) {
                        baseBitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(Color.WHITE);
                    }
                    //Record the coordinates of the point that the touch started
                    preX = startX = event.getX();
                    preY = startY = event.getY();
                    singlePath.moveTo(startX, startY);
                    break;
                // record the moving of finger on screen
                case MotionEvent.ACTION_MOVE:
                    //Record the coordinates of the moving points
                    float stopX = event.getX();
                    float stopY = event.getY();
                    paint.setStrokeWidth(radio);
                   //connect the each point, make the line smoother
                    singlePath.quadTo(preX, preY, stopX, stopY);
                   //draw all path in the list
                    for (int i = 0; i < lines.size(); i++)
                        canvas.drawPath(lines.get(i), paint);
                    canvas.drawPath(singlePath, paint);
                    //Update the location of the start point
                    preX = startX = event.getX();
                    preY = startY = event.getY();
                    //display image in imageview
                    iv.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP:
                    tryToTranslate(baseBitmap);
                    //save singlepath to the lines
                    lines.add(new Path(singlePath));
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

                    String resultJson = new TranslationAPI().getTransResult(translate_text, "auto", "zh");
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

    //save image to sd card
    protected void saveBitmap() {
        try {

            //clear cache
            result.destroyDrawingCache();
            //Set whether images can be cached
            result.setDrawingCacheEnabled(true);
            //if image already cached, return to bp.
            Bitmap bp = result.getDrawingCache();
            //copy bp
            Bitmap cache = Bitmap.createBitmap(bp);
            //clear drawing cache
            result.setDrawingCacheEnabled(false);

            Bitmap b2 = newBitmap(500, baseBitmap, cache);
            File dir = new File("/sdcard/writedown");
            //create a new file in sd card
            if(!dir.exists()) dir.mkdirs();
            String fileName = dir.getAbsolutePath()+"/"+filename;
            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            //save captured photos
            b2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Toast.makeText(WritingTable.this, "save successfully", Toast.LENGTH_SHORT).show();
            //check the SDK version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //scan the file,refresh the gallery
                final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                final Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            } else {
                final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(Environment.getExternalStorageDirectory()));
                sendBroadcast(intent);
            }


        } catch (Exception e) {
            Toast.makeText(WritingTable.this, "save failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    //merger the image
    public static Bitmap newBitmap(int width, Bitmap bit1, Bitmap bit2) {
        if (width <= 0) {
            return null;
        }
        //get height and width
        int h1 =  bit1.getHeight() * width / bit1.getWidth();
        int h2 = bit2.getHeight() * width / bit2.getWidth();
        int height = h1 + h2;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Log.i("tagg", h1+","+h2);
        //Scale to the specified size of the new bitmap
        Bitmap newSizeBitmap1 = getNewSizeBitmap(bit1, width, h1);
        Bitmap newSizeBitmap2 = getNewSizeBitmap(bit2, width, h2);

        //The bitmap is placed into the drawing area and the picture to be spliced is drawn into the specified memory area
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(newSizeBitmap1, 0, 0, null);
        canvas.drawBitmap(newSizeBitmap2, 0, h1, null);
        return bitmap;
    }

    public static Bitmap getNewSizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {

        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        //Gets the matrix parameter that want to scale
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        //get new image
        Bitmap bit1Scale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
        return bit1Scale;
    }

    //clear the canvas
    protected void resumeCanvas() {
        if (baseBitmap != null) {
            baseBitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            lines.clear();
            iv.setImageBitmap(baseBitmap);
            Toast.makeText(WritingTable.this, "clear canvas successfully", Toast.LENGTH_SHORT).show();
        }
    }

    protected void undo() {
        //delete a single stroke
        lines.remove(lines.size() - 1);
        canvas.drawPoint(0, 0, paint);
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < lines.size(); i++)
            canvas.drawPath(lines.get(i), paint);
        iv.setImageBitmap(baseBitmap);
        if(lines.size() == 0) btn_undo.setEnabled(false);
    }
}