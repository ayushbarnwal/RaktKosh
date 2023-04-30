package com.example.raktkosh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    final String USER_MODE_0 = "0";
    final String USER_MODE_1 = "1";
    final String USER_MODE_2 = "2";
    final String ADMIN_MODE_0 = "0";
    final String ADMIN_MODE_1 = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i;
                SharedPreferences sharedPreferences = getSharedPreferences("LogIn", MODE_PRIVATE);
                String user_log_in = sharedPreferences.getString("UserLogIn", "");
                String admin_log_in = sharedPreferences.getString("AdminLogIn", "");


                if(user_log_in.equals(USER_MODE_1)) {
                    i = new Intent(SplashActivity.this, UserProfileActivity.class);
                    startActivity(i);
                    finish();
                }else if(user_log_in.equals(USER_MODE_2)) {
                    i = new Intent(SplashActivity.this, UserMainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    i = new Intent(SplashActivity.this, LogInActivity.class);
                    startActivity(i);
                    finish();
                }

                if(admin_log_in.equals(ADMIN_MODE_1)){
                    i = new Intent(SplashActivity.this, AdminMainActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 2000);

    }
}