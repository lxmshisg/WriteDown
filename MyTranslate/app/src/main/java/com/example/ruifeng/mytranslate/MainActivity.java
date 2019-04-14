package com.example.ruifeng.mytranslate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruifeng.mytranslate.BaiduTranslate.TransApi;
import com.example.ruifeng.mytranslate.BaiduTranslate.TranslateResult;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class MainActivity extends AppCompatActivity {


  //  private Button button;
    private TextView textView;
    private Handler handler = new Handler();
    // private EditText editText;
    private ImageView imageView;
    private Button bthProcess;
  //  private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.image_view);
        bthProcess = (Button) findViewById(R.id.Button_process);
        final Bitmap bitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(), R.drawable.test // 改
        );
        imageView.setImageBitmap(bitmap);
        bthProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String resultJson = new TransApi().getTransResult(translate_text , "auto", "zh"); // query改动 翻译用
                            //get result and analyze the result
                            Gson gson = new Gson();
                            TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
                            final List<TranslateResult.TransResultBean> trans_result = translateResult.getTrans_result();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    String dst = "";
                                    for (TranslateResult.TransResultBean s : trans_result
                                    ) {
                                        dst = dst + "\n" + s.getDst();
                                    }

                                    textView.setText(dst);
                                }
                            });

                        }
                    }).start();


                }
            }
        });
    }
}
/*
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

              //      final String query = editText.getText().toString();
//                final String query = "";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String resultJson = new TransApi().getTransResult( "hello", "auto", "zh"); // query改动 翻译用
                            //get result and analyze the result
                            Gson gson = new Gson();
                            TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
                            final List<TranslateResult.TransResultBean> trans_result = translateResult.getTrans_result();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    String dst = "";
                                    for (TranslateResult.TransResultBean s : trans_result
                                    ) {
                                        dst = dst + "\n" + s.getDst();
                                    }

                                    textView.setText(dst);
                                }
                            });

                        }
                    }).start();
                }
            });

        }
    }

*/