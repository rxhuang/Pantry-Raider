package com.example.stevetran.pantryraider;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.stevetran.pantryraider.Login.LoginActivity;

/**
 * Created by Abel on 11/8/2017.
 * This is the loading(splash) page. It should call the login activity.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //start login page(main activity) here
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}