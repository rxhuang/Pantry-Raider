package com.example.stevetran.pantryraider.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stevetran.pantryraider.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
    }

    public void resetPressed(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener;
        EditText emailView = (EditText) findViewById(R.id.forgetreset_email);
        String email = emailView.getText().toString();

        // check validity
        if (email.length() <= 0) {
            emailView.setError("Please Enter Your Email");
            return;
        }
        if (!(email.contains("@") && (email.contains(".")))) {
            emailView.setError("Invalid Email");
            return;
        }

        mAuth.sendPasswordResetEmail(email);
        Toast.makeText(ForgetPasswordActivity.this, "A Verification E-mail Has Been Sent",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
