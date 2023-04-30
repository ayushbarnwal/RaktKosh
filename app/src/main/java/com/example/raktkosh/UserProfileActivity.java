package com.example.raktkosh;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.raktkosh.Models.User;
import com.example.raktkosh.Notification.ApiUtilities;
import com.example.raktkosh.Notification.PushNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String first_name, last_name, gender, Date_of_Birth, email_address, postal_address, blood_grp, city, state, country, pinCode;

    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar pb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        EditText dateOfBirth = (EditText) findViewById(R.id.date_of_birth);
        EditText firstName = (EditText) findViewById(R.id.first_name);
        EditText lastName = (EditText) findViewById(R.id.last_name);
        EditText dateofBirth = (EditText) findViewById(R.id.date_of_birth);
        EditText emailAddress = (EditText) findViewById(R.id.email_address);
        EditText address = (EditText) findViewById(R.id.address);
        EditText eCity = (EditText)findViewById(R.id.city);
        EditText eState = (EditText)findViewById(R.id.state);
        EditText eCOuntry = (EditText) findViewById(R.id.country);
        EditText ePinCode = (EditText)findViewById(R.id.pin_code);
        Button register = (Button) findViewById(R.id.register_button);
        pb1 = (ProgressBar) findViewById(R.id.progressBar3);
        pb1.setVisibility(View.GONE);

        String[] mgender = {"Select Gender", " Male", "Female", "Other"};
        String[] mbloodGroup = {"Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

        Spinner genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        Spinner bloodSpinner = (Spinner) findViewById(R.id.blood_spinner);


        ArrayAdapter genderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mgender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener( UserProfileActivity.this);

        ArrayAdapter bloodAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mbloodGroup);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(bloodAdapter);
        bloodSpinner.setOnItemSelectedListener(UserProfileActivity.this);

        dateofBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserProfileActivity.this, "please enter the DOB in dd/mm/yyyy format", Toast.LENGTH_LONG).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb1.setVisibility(View.VISIBLE);
                register.setVisibility(View.GONE);
                first_name = firstName.getText().toString().trim();
                last_name = lastName.getText().toString().trim();
                Date_of_Birth = dateOfBirth.getText().toString().trim();
                email_address = emailAddress.getText().toString().trim();
                postal_address = address.getText().toString().trim();
                city = eCity.getText().toString().trim();
                state = eState.getText().toString().trim();
                country = eCOuntry.getText().toString().trim();
                pinCode = ePinCode.getText().toString().trim();

                String id = auth.getCurrentUser().getUid().toString();
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("first_Name", first_name);
                obj.put("last_Name", last_name);
                obj.put("date_Of_Birth", Date_of_Birth);
                obj.put("email_Address", email_address);
                obj.put("blood_Group", blood_grp);
                obj.put("gender", gender);
                obj.put("postal_Address", postal_address);
                obj.put("City", city);
                obj.put("State", state);
                obj.put("Country", country);
                obj.put("pin_Code", pinCode);

                database.getReference().child("User").child(id).updateChildren(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        SharedPreferences pref = getSharedPreferences("LogIn", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("UserLogIn", "2");
                        editor.apply();

                        Intent i = new Intent(UserProfileActivity.this, UserMainActivity.class);
                        i.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });

            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()){
            case(R.id.gender_spinner):
                gender = adapterView.getItemAtPosition(position).toString();
                break;
            case(R.id.blood_spinner):
                blood_grp = adapterView.getItemAtPosition(position).toString();
                break;
                default:
                    Toast.makeText(UserProfileActivity.this, "Hello", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}