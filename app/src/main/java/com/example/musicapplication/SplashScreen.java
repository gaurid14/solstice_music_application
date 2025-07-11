package com.example.musicapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        ImageView logo = findViewById(R.id.logoImage);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(SignupScreen.SHARED_PREFS, MODE_PRIVATE);
            String user = sharedPreferences.getString(SignupScreen.USERNAME, null);
            String email = sharedPreferences.getString(SignupScreen.EMAIL, null);
            String password = sharedPreferences.getString(SignupScreen.PASSWORD, null);

            Log.e("user", String.valueOf(user));
            Log.e("email", String.valueOf(email));
            Log.e("password", String.valueOf(password));

            Intent intent;
//            if (user != null && email != null && password != null) {
//                intent = new Intent(SplashScreen.this, MainActivity.class); //
//            }
            if(user == null && email == null && password == null) {
                intent = new Intent(SplashScreen.this, SignupScreen.class);
            }else{
                intent = new Intent(SplashScreen.this, MainActivity.class);
            }

            startActivity(intent);
            finish();
        }, 1800); // 1.8 seconds
    }
}
