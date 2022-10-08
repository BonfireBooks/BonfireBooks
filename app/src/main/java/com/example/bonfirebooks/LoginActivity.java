package com.example.bonfirebooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private TextView forgotPass;

    private EditText txtE_HofID;
    private EditText txtE_password;

    private Button btn_login;
    private Button btn_signUp;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotPass = findViewById(R.id.txtV_forgot_password);
        txtE_HofID = findViewById(R.id.txtE_HofID);
        txtE_password = findViewById(R.id.txtE_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signup);
        btn_back = findViewById(R.id.btn_back);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginPassReset.class));
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInput()) {

                }
            }
        });
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // ToDo -- Add check to see if id exists in database

        if(TextUtils.isEmpty(txtE_HofID.getText().toString())) {
            txtE_HofID.setError("Please enter your Hofstra ID");
            isValid = false;
        }
        if(TextUtils.isEmpty(txtE_password.getText().toString())) {
            txtE_password.setError("Please enter your password");
            isValid = false;
        }

        return isValid;
    }
}