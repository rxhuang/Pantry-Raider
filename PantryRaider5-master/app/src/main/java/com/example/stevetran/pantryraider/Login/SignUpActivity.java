package com.example.stevetran.pantryraider.Login;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.icu.lang.UCharacterEnums;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stevetran.pantryraider.Home.HomeActivity;
import com.example.stevetran.pantryraider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


/**
 * Created by rongfalu on 11/21/17.
 */

class User {
    public String uid;
    public HashMap<String, Object> Preferences;
    public HashMap<String, Object> Allergies;
    public HashMap<String, Object> Ingredients;
    public HashMap<String, Object> Saved_Recipes;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, HashMap preference, HashMap allergy, HashMap ingredients){
        this.uid = uid;
        this.Preferences = preference;
        this.Allergies = allergy;
        this.Ingredients = ingredients;
    }

    public User(String uid, HashMap saved_recipes) {
        this.Saved_Recipes = saved_recipes;
    }
}


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

    }

    public void createPressed(View view) {
        final Intent goToHome = new Intent(this, HomeActivity.class);
        final Intent gotoLogin = new Intent(this, LoginActivity.class);
        EditText emailView = (EditText) findViewById(R.id.input_email);
        String email = emailView.getText().toString();
        EditText passwordView = (EditText) findViewById(R.id.input_password);
        String password = passwordView.getText().toString();
        EditText usernameView = (EditText) findViewById(R.id.input_name);
        String username = usernameView.getText().toString();
        EditText repasswordView = (EditText) findViewById(R.id.re_password);
        String repassword = repasswordView.getText().toString();
        int errorcount = 0;

        //check each blank
        if (username.length() <= 0) {
            usernameView.setError("Please Enter Your Name");
            errorcount++;
        }
        if (email.length() <= 0) {
            emailView.setError("Please Enter Your Email");
            errorcount++;
        }
        if (password.length() <= 0) {
            passwordView.setError("Please Enter Your Name");
            errorcount++;
        }
        if (repassword.length() <= 0) {
            repasswordView.setError("Please Confirm Your Password");
            errorcount++;
        }
        if (!repassword.equals(password)) {
            repasswordView.setText("");
            repasswordView.setError("Password Doesn't Match");
            errorcount++;
        }
        if (errorcount > 0) {
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            Toast.makeText(SignUpActivity.this, "A Verification E-mail Has Been Sent",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(gotoLogin);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        final DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();

                        // Attach a listener to read the data at our posts reference
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                System.out.println(user.uid);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });

                        // store info to fire base
                        //User user = new User(mAuth.getCurrentUser().getUid(), 0, 0);
                        HashMap<String, Object> allergiesMap = new HashMap<>();
                        HashMap<String, Object> prefMap = new HashMap<>();
                        HashMap<String, Object> ingredientsMap = new HashMap<>();
                        HashMap<String, Object> savedRecipesMap = new HashMap<>();

                        allergiesMap.put("dairy", false);
                        allergiesMap.put("egg", false);
                        allergiesMap.put("gluten", false);
                        allergiesMap.put("peanut", false);
                        allergiesMap.put("seafood", false);
                        allergiesMap.put("sesame", false);
                        allergiesMap.put("soy", false);
                        allergiesMap.put("treenut", false);
                        allergiesMap.put("wheat", false);

                        prefMap.put("lactovegetarian", false);
                        prefMap.put("ovovegetarian", false);
                        prefMap.put("pescetarian", false);
                        prefMap.put("vegan", false);
                        prefMap.put("vegetarian", false);

                        ingredientsMap.put("dummy", 999999);

                        savedRecipesMap.put("r-1", "-1");

                        User user = new User(mAuth.getCurrentUser().getUid(), allergiesMap, prefMap, ingredientsMap);
                        User savedRecFBUser = new User(mAuth.getCurrentUser().getUid(), savedRecipesMap);

                        mDatabase.child("Account").child(mAuth.getCurrentUser().getUid()).setValue(user);
                        mDatabase.child("Saved_Recipes").child(mAuth.getCurrentUser().getUid()).setValue(savedRecipesMap);

                        // ...
                    }
                });
    }

    public void alreadyMemberLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
    }



    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
