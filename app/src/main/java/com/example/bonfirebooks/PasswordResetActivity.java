package com.example.bonfirebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class PasswordResetActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private Button btn_Back;
    private Button btn_reset_pass;
    private EditText txtE_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pass_reset);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_Back = findViewById(R.id.btn_back);
        btn_reset_pass = findViewById(R.id.btn_reset_pass);
        txtE_email = findViewById(R.id.txtE_email);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInput()) {
                    txtE_email.setError(null);

                    // send the reset email
                    firebaseAuth.sendPasswordResetEmail(txtE_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // notify the user the email was sent and navigate back to login
                                Log.d("sendResetPass", "Successful");
                                Toast.makeText(PasswordResetActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.d("sendResetPass", "Failed");
                                txtE_email.setError("Could not locate account");
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // email check -- must use hofstra domain
        String email = txtE_email.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(email)) {
            txtE_email.setError("Please enter your Hofstra email");
            isValid = false;
        } else if (!email.contains("pride.hofstra.edu") || !isValidEmail(email)) {
            txtE_email.setError("Please enter a valid Hofstra email");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        String regex = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return Pattern.compile(regex).matcher(email).matches();
    }
}