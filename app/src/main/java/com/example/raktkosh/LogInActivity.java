package com.example.raktkosh;

import static android.widget.Toast.LENGTH_LONG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    String id;
    String mVerificationId;
    EditText phone_No;
    String number;
    private ProgressBar pb;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();

        Button log_in_btn;
        button = findViewById(R.id.admin_mode);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity.this, AdminLogInActivity.class);
                startActivity(i);
                finish();
            }
        });

        phone_No = findViewById(R.id.phone_no);
        log_in_btn = findViewById(R.id.log_in);
        pb = (ProgressBar) findViewById(R.id.progress_bar);
        pb.setVisibility(View.GONE);

        log_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = phone_No.getText().toString().trim();
                Log.e("TAG", number);

                if(TextUtils.isEmpty(number)){
                    Toast.makeText(LogInActivity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                }else if(phone_No.length()!=10){
                    Toast.makeText(LogInActivity.this, "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
                }else{
                    log_in_btn.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);

                    otpsend();
                }
            }
        });

    }

    private void otpsend() {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks Callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LogInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.e("TAG",verificationId);
                Intent i = new Intent(LogInActivity.this, OtpActivity.class);
                i.putExtra("ph_number",phone_No.getText().toString().trim());
                i.putExtra("verification_Id", verificationId);
                startActivity(i);
            }
        };

        Log.e("TAG","103");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+91"+number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(LogInActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(Callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        Log.e("TAG","104");
        PhoneAuthProvider.verifyPhoneNumber(options);

        Log.e("TAG","102");

    }

//    private void signInWithCredential(PhoneAuthCredential credential) {
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.e("TAG","code verification completed");
//
//                            // Sign in success, update UI with the signed-in user's information
//                            // Log.d(TAG, "signInWithCredential:success");
//
//
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
//
//                        }
//                    }
//                }
//    );}


}