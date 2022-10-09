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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                    // get the user email -- login gets called from this function
                    getUserEmail(txtE_HofID.getText().toString());
                }
            }
        });
    }

    private void getUserEmail(String hofID) {
        // grab the user email from the realtime database
        DatabaseReference realtime = FirebaseDatabase.getInstance().getReference();
        realtime.child("users").child(hofID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d("getRealtimeUser", "success");
                    String email = String.valueOf(task.getResult().getValue());

                    // call the login here -- to make sure email has been retreived
                    loginUser(email, txtE_password.getText().toString());
                } else {
                    Log.w("getRealtimeUser", "failure\n" + task.getException());
                    Toast.makeText(LoginActivity.this, "Could not log in at the moment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        // login the user with the fetched email and the password entered by the user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("loginUser", "success");
                // navigate to main activity
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("loginUser", "failure\n" + e.getMessage());
                Toast.makeText(LoginActivity.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidInput() {
        final boolean[] isValid = {true};

        // check that ID field is empty
        if(TextUtils.isEmpty(txtE_HofID.getText().toString())) {
            txtE_HofID.setError("Please enter your Hofstra ID");
            isValid[0] = false;
        } else {
            // check if ID exists
            FirebaseDatabase.getInstance().getReference().child("users").child(txtE_HofID.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()) {
                        isValid[0] = false;
                        txtE_HofID.setError("Account does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // check that password field is empty
        if(TextUtils.isEmpty(txtE_password.getText().toString())) {
            txtE_password.setError("Please enter your password");
            isValid[0] = false;
        }

        return isValid[0];
    }
}