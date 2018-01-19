package com.example.stevetran.pantryraider.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stevetran.pantryraider.Home.HomeActivity;
import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Util.SharedConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by rongfalu on 11/21/17.
 */

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "LoginActivity";

    public void signUpPressed(View view) {
        Intent goToSignUp = new Intent(this, SignUpActivity.class);
        startActivity(goToSignUp);
    }

    public void loginPressed(View view) {
        final Intent goToHome = new Intent(this, HomeActivity.class);
        EditText emailView = (EditText) findViewById(R.id.email);
        String email = emailView.getText().toString();
        EditText passwordView = (EditText) findViewById(R.id.password);
        String password = passwordView.getText().toString();
        if(password.equals("") || email.equals("")) {
            Toast.makeText(LoginActivity.this, "E-mail/Password cannot be empty!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(!user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "E-mail Not Verified", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            SharedConstants.FIREBASE_USER_ID = user.getUid();
                            startActivity(goToHome);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void forgetPressed(View view) {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupFirebaseAuth();
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setting up firebase auth");

        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            SharedConstants.FIREBASE_USER_ID = mAuth.getCurrentUser().getUid();
            Intent goToHome = new Intent(this, HomeActivity.class);
            startActivity(goToHome);
            Toast.makeText(LoginActivity.this, "Logged In As: " + mAuth.getCurrentUser().getEmail(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
