package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.raktkosh.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditUserProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Toolbar tb1;

    FirebaseAuth auth;
    FirebaseDatabase database;
    StorageReference storageReference;
    String id;

    EditText eFirstName, eLastName, eDateOfBirth, eEmailAddress, eAddressLine, eCity, eState, eCountry, ePinCode;
    String first_name, last_name, gender, Date_of_Birth, email_address, postal_address, blood_grp, city, state, country, pinCode;
    Spinner eBloodSpinner, eGenderSpinner;
    ImageView userProfilePic;
    ProgressBar pb;
    Button updateBtn;
    ArrayAdapter bloodAdapter, genderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        tb1 = findViewById(R.id.tool_bar_3);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        InitializeWidget();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid().toString();

        String[] mgender = {"Select Gender", " Male", "Female", "Other"};
        String[] mbloodGroup = {"Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

        genderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mgender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eGenderSpinner.setAdapter(genderAdapter);
        eGenderSpinner.setOnItemSelectedListener(EditUserProfileActivity.this);

        bloodAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mbloodGroup);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eBloodSpinner.setAdapter(bloodAdapter);
        eBloodSpinner.setOnItemSelectedListener(EditUserProfileActivity.this);

        fetchUserDetails();

        loadProfilePicture();

        updateProfile();

    }

        private void updateProfile() {

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                //updateBtn.setVisibility(View.GONE);
                first_name = eFirstName.getText().toString().trim();
                last_name = eLastName.getText().toString().trim();
                Date_of_Birth = eDateOfBirth.getText().toString().trim();
                email_address = eEmailAddress.getText().toString().trim();
                postal_address = eAddressLine.getText().toString().trim();
                city = eCity.getText().toString().trim();
                state = eState.getText().toString().trim();
                country = eCountry.getText().toString().trim();
                pinCode = ePinCode.getText().toString().trim();

                String id = auth.getCurrentUser().getUid().toString();
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("first_Name", first_name);
                obj.put("last_Name", last_name);
                obj.put("date_Of_Birth", Date_of_Birth);
                obj.put("email_Address", email_address);
//                obj.put("blood_Group", blood_grp);
//                obj.put("gender", gender);
                obj.put("postal_Address", postal_address);
                obj.put("City", city);
                obj.put("State", state);
                obj.put("Country", country);
                obj.put("pin_Code", pinCode);

                database.getReference().child("User").child(id).updateChildren(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EditUserProfileActivity.this,"Updated", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(EditUserProfileActivity.this, UserMainActivity.class);
                        startActivity(i);
                    }
                });
            }
        });

    }

    private void loadProfilePicture() {
        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,33);
            }
        });
    }

    private void InitializeWidget() {
        eGenderSpinner = findViewById(R.id.edit_gender_spinner);
        eBloodSpinner = findViewById(R.id.edit_blood_spinner);

        eFirstName = findViewById(R.id.edit_first_name);
        eLastName = findViewById(R.id.edit_last_name);
        eDateOfBirth = findViewById(R.id.edit_date_of_birth);
        eEmailAddress = findViewById(R.id.edit_email_address);
        eAddressLine = findViewById(R.id.edit_address);
        eCity = findViewById(R.id.editTextTextPersonName);
        eState = findViewById(R.id.editTextTextPersonName3);
        eCountry = findViewById(R.id.editTextTextPersonName4);
        ePinCode = findViewById(R.id.editTextTextPersonName5);
        updateBtn = findViewById(R.id.update_button);

        userProfilePic = findViewById(R.id.edit_profile_picture);
        pb = findViewById(R.id.progressBar4);

    }

    private void fetchUserDetails() {

        database.getReference().child("User").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user.setUserId(id);
                eFirstName.setText(user.getFirst_Name());
                eLastName.setText(user.getLast_Name());
                eDateOfBirth.setText(user.getDate_Of_Birth());
                eAddressLine.setText(user.getPostal_Address());
                eCity.setText(user.getCity());
                eState.setText(user.getState());
                eCountry.setText(user.getCountry());
                ePinCode.setText(user.getPin_Code());
                eEmailAddress.setText(user.getEmail_Address());
                eBloodSpinner.setSelection(bloodAdapter.getPosition(user.getBlood_Group()));
                eGenderSpinner.setSelection(genderAdapter.getPosition(user.getGender()));
                Glide.with(EditUserProfileActivity.this).load(user.getProfilePic()).placeholder(R.drawable.user).into(userProfilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case(R.id.gender_spinner):
                gender = parent.getItemAtPosition(position).toString();
                break;
            case(R.id.blood_spinner):
                blood_grp = parent.getItemAtPosition(position).toString();
                break;
            default:
                Toast.makeText(EditUserProfileActivity.this, "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData()!=null){       //if user selected any image then getdata will give path
            Uri sFile = data.getData();
            userProfilePic.setImageURI(sFile);

            storageReference = FirebaseStorage.getInstance().getReference().child("UserProfilePic")
                    .child(auth.getCurrentUser().getUid());
            storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String , Object> obj = new HashMap<>();     // to update on firebase
                            obj.put("profilePic",uri.toString());
                            database.getReference().child("User").child(id)
                                    .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(EditUserProfileActivity.this,"Profile Pic Updated",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });


                }
            });


        }

    }
}