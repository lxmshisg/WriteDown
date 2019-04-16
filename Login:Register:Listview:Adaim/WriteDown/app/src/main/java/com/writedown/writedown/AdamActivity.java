package com.writedown.writedown;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.writedown.writedown.dbHelper.COL_5;

public class AdamActivity extends AppCompatActivity {

    dbHelper db;
    Button mButtonhis;
    Button btnDelete;
    EditText mTextUsernameD;
    Button btn_update;
    EditText usertype;
    Button btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adam);
        db = new dbHelper(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        mButtonhis = (Button) findViewById(R.id.button_viewAll);
        btnDelete = (Button) findViewById(R.id.button_delete);
        mTextUsernameD = (EditText) findViewById(R.id.login_accountD);
        btn_update =(Button)findViewById(R.id.button_update) ;
        usertype = (EditText) findViewById(R.id.usertype);
        btn_exit = (Button)findViewById(R.id.button_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToLogin = new Intent(AdamActivity.this, LoginActivity.class);
                startActivity(moveToLogin);
                System.exit(0);
            }
        });
        mButtonhis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = db.getAllData();
                if(res.getCount() == 0) {
                    // show message
                    showMessage("Error","Nothing found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("ID:"+ res.getString(0)+"\n");
                    buffer.append("Name :"+ res.getString( 1)+"\n");
                    buffer.append("Password :"+ res.getString(2)+"\n");
                    buffer.append("Type :"+ res.getString(3)+"\n");
                    buffer.append("Filename :"+ res.getString(4)+"\n");
                    buffer.append("item :"+ res.getString(5)+"\n");
                }

                // Show all data
                showMessage("Data",buffer.toString());
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer deletedRows = db.deleteData(mTextUsernameD.getText().toString());
                if (deletedRows > 0)
                    Toast.makeText(AdamActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AdamActivity.this, "Data not Deleted", Toast.LENGTH_LONG).show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUpdate = db.updateData(mTextUsernameD.getText().toString(),
                        usertype.getText().toString());
                if(isUpdate == true)
                if (isUpdate == true)
                    Toast.makeText(AdamActivity.this, "Data Updated", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AdamActivity.this, "Data not Updated", Toast.LENGTH_LONG).show();
            }
        });




    }
    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }


}
