package com.example.raktkosh;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.raktkosh.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    EditText otp;
    private ProgressBar pb2;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        otp = (EditText) findViewById(R.id.editTextTextPersonName2);
        pb2 = (ProgressBar) findViewById(R.id.progressbar);
        pb2.setVisibility(View.GONE);

        phoneNumber = getIntent().getStringExtra("ph_number");

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.button2).setVisibility(View.GONE);
                pb2.setVisibility(View.VISIBLE);
                verifyOtp();
            }
        });


    }

    private void verifyOtp() {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(getIntent().getStringExtra("verification_Id"), otp.getText().toString().trim());

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user1 = new User(phoneNumber);
                            String id = task.getResult().getUser().getUid();
                            Log.e("TAG", "h1");
                            database.getReference().child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        Log.e("TAG", "h4");
                                        SharedPreferences pref = getSharedPreferences("LogIn", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("UserLogIn", "2");
                                        editor.apply();

                                        Intent i = new Intent(OtpActivity.this, UserMainActivity.class);
                                        i.setFlags( i.FLAG_ACTIVITY_CLEAR_TASK | i.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        Log.e("TAG", "h5");
                                    }
                                    else{
                                        Log.e("TAG", "Success1");
                                        database.getReference().child("User").child(id).setValue(user1);
                                        SharedPreferences pref = getSharedPreferences("LogIn", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("UserLogIn", "1");
                                        editor.apply();
                                        Log.e("TAG", "h7");
                                        Intent i = new Intent(OtpActivity.this, UserProfileActivity.class);
                                        i.setFlags( i.FLAG_ACTIVITY_CLEAR_TASK | i.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        Log.e("TAG", "h8");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}