package com.writedown.writedown;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.writedown.writedown.dbHelper.COL_1;
import static com.writedown.writedown.dbHelper.COL_2;

public class HistoryActivity extends AppCompatActivity {
    ListView listView;
    private dbHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        ListView listView = (ListView) findViewById(R.id.listview);
        dbHelper = new dbHelper(this);
        ArrayList<String> arrayList = new ArrayList<>();

        Cursor data = dbHelper.getAllData();

        if (data.getCount() == 0){
            Toast.makeText(HistoryActivity.this,"Empty",Toast.LENGTH_LONG).show();
        }else {
            while (data.moveToNext()){
                arrayList.add(data.getString(0));
                arrayList.add(data.getString(2));
                ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(arrayAdapter);
            }
        }




    }
}
