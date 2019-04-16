package com.writedown.writedown;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {
    dbHelper db;
    Button change;
    Button cancel;
    EditText usernameU;
    EditText userpasswordU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        db = new dbHelper(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        change = (Button) findViewById(R.id.btn_change);
        usernameU= (EditText) findViewById(R.id.login_accountU);
        userpasswordU= (EditText) findViewById(R.id.login_passwordU);
        cancel =(Button)findViewById(R.id.btn_cancel);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChange = db.ChangePWD(usernameU.getText().toString(),
                        userpasswordU.getText().toString());
                 boolean rs =db.checkUserR(usernameU.getText().toString());
                if(rs == false){
                    Toast.makeText(UpdateActivity.this, "Account does not exists!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (isChange == true)
                    if (isChange == true)
                        Toast.makeText(UpdateActivity.this, "Password Changed", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(UpdateActivity.this, "Password Change failed", Toast.LENGTH_LONG).show();

            }


        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(back);
            }
        });
    }

}


