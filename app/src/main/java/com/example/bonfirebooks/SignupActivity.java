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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private Button btn_Back;
    private Button btn_Signup;

    private EditText txtE_displayName;
    private EditText txtE_HofID;
    private EditText txtE_email;
    private EditText txtE_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_Back = findViewById(R.id.btn_back);
        btn_Signup = findViewById(R.id.btn_signup);
        txtE_displayName = findViewById(R.id.txtE_displayName);
        txtE_HofID = findViewById(R.id.txtE_HofID);
        txtE_email = findViewById(R.id.txtE_email);
        txtE_password = findViewById(R.id.txtE_password);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInput()) {
                    createUser();
                }
            }
        });
    }

    private void createUser() {
        // input data
        String displayName = txtE_displayName.getText().toString();
        String hofstraID = txtE_HofID.getText().toString();
        String email = txtE_email.getText().toString();
        String password = txtE_password.getText().toString();

        // create user in auth system
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d("createUser", "success");
                    String uID = auth.getCurrentUser().getUid();

                    // create user data in realtime and firestore databases
                    createUserInRealtime(hofstraID, email);
                    createUserInFirestore(uID, displayName, email, hofstraID);
                } else {
                    Log.w("createUser:failure", task.getException().toString());
                    Toast.makeText(SignupActivity.this, "Could not create an account at this time.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUserInFirestore(String uID, String displayName, String email, String hofID) {
        // create Map with users info
        HashMap<String, String> userInfo = new HashMap();
        userInfo.put("name", displayName);
        userInfo.put("email", email);
        userInfo.put("hofID", hofID);

        // Set users personal info
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(uID).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void createUserInRealtime(String hofId, String email) {
        // store the hofId:email in realtime
        DatabaseReference realtime = FirebaseDatabase.getInstance().getReference();
        realtime.child("users").child(hofId).setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("createUserRealtime", "success");
                } else {
                    Log.w("createUserRealtime", "failure\n" + task.getException().toString());
                    Toast.makeText(SignupActivity.this, "Could not create an account at this time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // display name check
        if(TextUtils.isEmpty(txtE_displayName.getText().toString())) {
            txtE_displayName.setError("Please enter a display name");
            isValid = false;
        }

        // Hofstra ID check
        String id = txtE_HofID.getText().toString().toLowerCase();

        if(TextUtils.isEmpty(id)) {
            txtE_HofID.setError("Please enter your Hofstra ID");
            isValid = false;
        } else if(id.charAt(0) != 'h' || id.length() != 10) {
            txtE_HofID.setError("Please enter a valid Hofstra ID");
            isValid = false;
        } else {
            // check all chars after 'h' are numbers
            try {
                Integer.parseInt(id.substring(1));
            } catch (NumberFormatException e) {
                txtE_HofID.setError("Please enter a valid Hofstra ID");
                isValid = false;
            }
        }

        // email check -- must use hofstra domain
        String email = txtE_email.getText().toString().toLowerCase();
        if(TextUtils.isEmpty(email)) {
            txtE_email.setError("Please enter your Hofstra email");
            isValid = false;
        } else if(!email.contains("pride.hofstra.edu") || !isValidEmail(email)) {
            txtE_email.setError("Please enter a valid Hofstra email");
            isValid = false;
        }

        // password check
        String pass = txtE_password.getText().toString();
        if(TextUtils.isEmpty(pass)) {
            txtE_password.setError("Please enter a password");
        }
        else if(pass.length() < 6) {
            txtE_password.setError("Password must have 6 or more characters");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        String regex = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return Pattern.compile(regex).matcher(email).matches();
    }
}