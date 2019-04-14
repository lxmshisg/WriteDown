package com.writedown.writedown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextrepassword;
    Button mButtonRegister;
    TextView getViewLogin;
    dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new dbHelper(this);
        mTextUsername = (EditText) findViewById(R.id.login_account);
        mTextPassword = (EditText) findViewById(R.id.login_password);
        mTextrepassword = (EditText) findViewById(R.id.login_re_password);
        mButtonRegister = (Button) findViewById(R.id.register_btn);
        getViewLogin = (TextView) findViewById(R.id.login);
        getViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        });
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mTextUsername.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                String re_pwd = mTextrepassword.getText().toString().trim();
                boolean rs = db.checkUserR(user);

                if (!(user.length() >= 6 && user.length() <= 20)) {
                    Toast.makeText(RegisterActivity.this, "Username must at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(pwd.length() > 0 && re_pwd.length() > 0)) {
                    Toast.makeText(RegisterActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!re_pwd.equals(pwd)) {
                    Toast.makeText(RegisterActivity.this, "Two passwords are not matching", Toast.LENGTH_SHORT).show();
                    return;

                }if(user.equals("123321")&&pwd.equals("123321")) {
                    Toast.makeText(RegisterActivity.this, "Account exists", Toast.LENGTH_SHORT).show();
                }
                if(rs == false){
                    db.addUser(user, pwd);
                    Toast.makeText(RegisterActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    Intent moveToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(moveToLogin);
                    return;

                }else{
                    Toast.makeText(RegisterActivity.this, "Account exists", Toast.LENGTH_SHORT).show();

                }


            }


        });

    }
}
