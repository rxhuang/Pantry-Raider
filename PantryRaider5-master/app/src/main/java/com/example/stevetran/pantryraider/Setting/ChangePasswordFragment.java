package com.example.stevetran.pantryraider.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stevetran.pantryraider.Login.LoginActivity;
import com.example.stevetran.pantryraider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {
    View view;
    // get current logged in user's info
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String email = mAuth.getCurrentUser().getEmail();

    // text initialize
    TextView oldpassword;
    TextView newpassword;
    TextView retypepassword;

    // button initialize
    Button btnChangePassword;
    Button btnClear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        //overwrites default backbutton
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK) {

                    ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.container);
                    viewPager.setCurrentItem(0);

                    //set up a new title
                    TextView mTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
                    mTitle.setText("Setting");

                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        oldpassword = view.findViewById(R.id.CPoldpassword);
        newpassword = view.findViewById(R.id.CPnewpassword);
        retypepassword = view.findViewById(R.id.CPretypepassword);
        btnChangePassword = (Button) view.findViewById(R.id.BtnChangePassword);
        btnClear = (Button) view.findViewById(R.id.Clear);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                ///TextView email = (TextView) findViewById(R.id.CPemail);

                if (oldpassword.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter the Old Password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newpassword.getText().toString().isEmpty() || retypepassword.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter and Verify the New Password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newpassword.getText().toString().equals(retypepassword.getText().toString())) {
                    Toast.makeText(getContext(), "Password Doesn't Match",
                            Toast.LENGTH_SHORT).show();
                    newpassword.setText("");
                    retypepassword.setText("");
                    return;
                }

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Get auth credentials from the user for re-authentication. The example below shows
                // email and password credentials but there are multiple possible providers,
                // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, oldpassword.getText().toString());

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(retypepassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("CP", "Password updated");
                                                Toast.makeText(getContext(), "Password Updated",
                                                        Toast.LENGTH_SHORT).show();
                                                // log out current user
                                                mAuth.signOut();
                                                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            } else {
                                                Log.d("CP", "Error password not updated");
                                                Toast.makeText(getContext(), "Error password not updated",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("CP", "Error auth failed");
                                    Toast.makeText(getContext(), "Incorrect Old Password",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                oldpassword.setText("");
                newpassword.setText("");
                retypepassword.setText("");
            }
        });
    }
}