package com.writedown.writedown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText mTextUsernameL;
    EditText mTextPasswordL;
    Button   mButtonLogin;
    TextView getViewRegister;
    dbHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new dbHelper(this);
        mTextUsernameL = (EditText) findViewById(R.id.login_accountL);
        mTextPasswordL = (EditText) findViewById(R.id.login_passwordL);
        mButtonLogin = (Button) findViewById(R.id.login_btn);
        getViewRegister = (TextView) findViewById(R.id.register);
        getViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mTextUsernameL.getText().toString().trim();
                String pwd = mTextPasswordL.getText().toString().trim();
                boolean res = db.checkUser(user, pwd);

                if(!(user.length()>=6&&user.length()<=10)) {
                    Toast.makeText(LoginActivity.this, "Enter the username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(pwd.length()>0)) {
                    Toast.makeText(LoginActivity.this, "Enter the password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (res == true) {
                    Intent moveToMain = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(moveToMain);
                } else {
                    Toast.makeText(LoginActivity.this, "Username or Password is not correct", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}