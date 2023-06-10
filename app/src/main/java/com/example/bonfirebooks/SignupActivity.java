package com.example.bonfirebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private Button btn_Back;
    private Button btn_Signup;

    private EditText txtE_displayName;
    private EditText txtE_phone_number;
    private EditText txtE_email;
    private EditText txtE_password;
    private EditText txtE_confirm_password;

    String displayName;
    String email;
    String password;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_Back = findViewById(R.id.btn_back);
        btn_Signup = findViewById(R.id.btn_signup);
        txtE_displayName = findViewById(R.id.txtE_displayName);
        txtE_phone_number = findViewById(R.id.txtE_phone_number);
        txtE_email = findViewById(R.id.txtE_email);
        txtE_password = findViewById(R.id.txtE_password);
        txtE_confirm_password = findViewById(R.id.txtE_confirm_password);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayName = txtE_displayName.getText().toString();
                email = txtE_email.getText().toString();
                password = txtE_password.getText().toString();
                phoneNumber = txtE_phone_number.getText().toString();

                if (isValidInput()) {
                    createUser();
                }
            }
        });

        // properly format the phone number
        txtE_phone_number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void createUser() {
        // create user in auth system
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("createUser", "success");
                    String uID = auth.getCurrentUser().getUid();

                    // send email verification
                    sendEmailVerification();

                    // create user data in firestore database
                    createUserInFirestore(uID, displayName, email, phoneNumber);
                } else {
                    Log.w("createUser:failure", task.getException().toString());
                    Toast.makeText(SignupActivity.this, "Could not create an account at this time.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("emailVerificationSent", "success");
                } else {
                    Log.d("emailVerificationSent", "failure");
                }
            }
        });

        // sign out since users should be able to login without verifying their email
        auth.signOut();
    }

    private void createUserInFirestore(String uID, String displayName, String email, String phoneNumber) {
        // create Map with users info
        HashMap<String, String> userInfo = new HashMap();
        userInfo.put("name", displayName);
        userInfo.put("email", email);

        if (phoneNumber != null) {
            if (!phoneNumber.isEmpty()) {
                userInfo.put("phoneNumber", phoneNumber);
            }
        }

        // Set users personal info
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(uID).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SignupActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // display name check
        if (TextUtils.isEmpty(txtE_displayName.getText().toString())) {
            txtE_displayName.setError("Please enter a display name");
            isValid = false;
        }

        // phone number check
        String phoneNumber = txtE_phone_number.getText().toString();
        phoneNumber = phoneNumber.replace("(", "");
        phoneNumber = phoneNumber.replace(")", "");
        phoneNumber = phoneNumber.replace("-", "");
        phoneNumber = phoneNumber.replace(" ", "");
        this.phoneNumber = phoneNumber;

        if (TextUtils.isEmpty(phoneNumber)) {
            txtE_phone_number.setError("Please enter your phone number");
            isValid = false;
        } else if (phoneNumber.length() != 10) {
            txtE_phone_number.setError("Please enter a valid 10 digit phone number");
            isValid = false;
        }

        // email check
        String email = txtE_email.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(email)) {
            txtE_email.setError("Please enter your email");
            isValid = false;
        } else if (!isValidEmail(email)) {
            txtE_email.setError("Please enter a valid email");
            isValid = false;
        }

        // password check
        String pass = txtE_password.getText().toString();
        String conf_pass = txtE_confirm_password.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            txtE_password.setError("Please enter a password");
        } else if (pass.length() < 6) {
            txtE_password.setError("Password must have 6 or more characters");
            isValid = false;
        } else if (!pass.equals(conf_pass)) {
            txtE_password.setError("Passwords are not different");
            txtE_confirm_password.setError("Passwords are not different");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        String regex = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return Pattern.compile(regex).matcher(email).matches();
    }
}