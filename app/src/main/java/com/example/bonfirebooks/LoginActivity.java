package com.example.bonfirebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private TextView forgotPass;

    private EditText txtE_email;
    private EditText txtE_password;

    private Button btn_login;
    private Button btn_signUp;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        forgotPass = findViewById(R.id.txtV_forgot_password);
        txtE_email = findViewById(R.id.txtE_email);
        txtE_password = findViewById(R.id.txtE_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signup);
        btn_back = findViewById(R.id.btn_back);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
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
                    txtE_password.setError(null);
                    loginUser(txtE_email.getText().toString(), txtE_password.getText().toString());
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        // login the user with the fetched email and the password entered by the user
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("loginUser", "success");
                emailIsVerified();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("loginUser", "failure\n" + e.getMessage());
                Toast.makeText(LoginActivity.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void emailIsVerified() {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if(firebaseUser.isEmailVerified()) {
            User user = new User();
            user.setUid(firebaseUser.getUid());
            user.setUser(user, LoginActivity.this);
        } else {
            // Todo -- notify user that they must first verify their email
            Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();

            // send the user a verification email
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Log.d("emailVerificationSent", "success");
                    } else {
                        Log.d("emailVerificationSent", "failure");
                    }
                }
            });

            auth.signOut();
        }
    }

    private boolean isValidInput() {
        final boolean[] isValid = {true};

        // check that ID field is empty

        // check that password field is empty
        if(TextUtils.isEmpty(txtE_password.getText().toString())) {
            txtE_password.setError("Please enter your password");
            isValid[0] = false;
        }

        return isValid[0];
    }
}