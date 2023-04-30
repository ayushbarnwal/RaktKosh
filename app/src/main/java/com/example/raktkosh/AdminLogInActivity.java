package com.example.raktkosh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AdminLogInActivity extends AppCompatActivity {

    CheckBox checkBox;
    String mVerificationId;
    EditText adminId, password;
    private ProgressBar pb;
    Button log_in_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in);

        adminId = findViewById(R.id.admin_id);
        password = findViewById(R.id.password);
        pb = (ProgressBar) findViewById(R.id.progress_bar);
        pb.setVisibility(View.GONE);

        log_in_btn = findViewById(R.id.log_in1);
        Button btn = findViewById(R.id.user_mode);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminLogInActivity.this, LogInActivity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        log_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_in_btn.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
                String admin_Id = adminId.getText().toString().trim();
                String admin_password = password.getText().toString().trim();
                if(TextUtils.isEmpty(admin_Id) || TextUtils.isEmpty(admin_password)){
                    Toast.makeText(AdminLogInActivity.this, "Please enter Admin ID", Toast.LENGTH_SHORT).show();
                }else{
                    log_in_btn.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.GONE);
                    if(admin_Id.equals("Admin") && admin_password.equals("Admin")){

                        SharedPreferences pref = getSharedPreferences("LogIn", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("AdminLogIn", "1");
                        editor.apply();

                        Intent i = new Intent(AdminLogInActivity.this, AdminMainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }


            }
        });
    }
}