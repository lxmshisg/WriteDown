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
    EditText mTextUsername;
    EditText mTextPassword;
    Button   mButtonLogin;
    TextView getViewRegister;
    dbHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new dbHelper(this);
        mTextUsername = (EditText) findViewById(R.id.login_account);
        mTextPassword = (EditText) findViewById(R.id.login_password);
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
                String user = mTextUsername.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                boolean res = db.checkUser(user, pwd);
                if (res == true) {
                    Intent moveToHistory = new Intent(LoginActivity.this, HistoryActivity.class);
                    startActivity(moveToHistory);
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}