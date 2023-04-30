package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raktkosh.Models.DatePickerFragment;
import com.example.raktkosh.Models.Message;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.Notification.ApiUtilities;
import com.example.raktkosh.Notification.NotificationData;
import com.example.raktkosh.Notification.PushNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BloodRequestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    FirebaseAuth auth;
    FirebaseDatabase database;
    RadioGroup radioGroup1;
    RadioGroup radioGroup2;
    String firstName, lastName, phoneNo, selectedType, haveReplacement, blood_grp, required_upto_date, blood_unit, hospital_name, verified, id;
    Button submitRequest;
    Spinner blood_grp_spinner;
    TextView requiredUpto, userFirstName, userLastName, userContactNo, upload_document;
    EditText eBloodUnit, eHospitalName;
    ImageView pdf;
    StorageReference storageReference;
    String pdf_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request);

        Toolbar tb = findViewById(R.id.tool_bar_4);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid();

        InitializeWidget();
        pdf.setVisibility(View.GONE);

        String[] bloodGroupList = {"Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

        ArrayAdapter bloodAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupList);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blood_grp_spinner.setAdapter(bloodAdapter);
        blood_grp_spinner.setOnItemSelectedListener(BloodRequestActivity.this);

        requiredUpto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date Picker");
            }
        });
        fetchUserDetails();

        findViewById(R.id.upload_document).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BloodRequestActivity.this, "file", Toast.LENGTH_SHORT).show();
                Intent i1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i1.setType("application/pdf");
                i1.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(i1,35);
            }
        });

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBloodRequest();
            }
        });

    }

    private void sendBloodRequest() {
        RadioButton rb1 = radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId());
        selectedType = rb1.getText().toString();
        RadioButton rb2 = radioGroup2.findViewById(radioGroup2.getCheckedRadioButtonId());
        haveReplacement = rb2.getText().toString();

        blood_unit = eBloodUnit.getText().toString();
        hospital_name = eHospitalName.getText().toString();
        User user = new User(firstName, lastName, selectedType, haveReplacement, blood_grp, blood_unit, hospital_name, phoneNo, required_upto_date, id, "No");
        user.setRequest_status("Active");
        database.getReference().child("Blood Request List").child(id).push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                database.getReference().child("Blood Request List").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            String key = dataSnapshot.getKey();
                            HashMap<String, Object> obj = new HashMap<>();
                            obj.put("request_id", key);
                            obj.put("unit_fulfilled", "0");
                            obj.put("user_accepted_request", "0");
                            obj.put("patient_situation", "Normal");
                            obj.put("SosRequested", "No");
                            obj.put("document_pdf", pdf_url);
                            if(!dataSnapshot.child("request_id").exists()){
                                database.getReference().child("Blood Request List").child(id).child(key).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        String titletxt = "Blood Request Raised";
                                        String msgTxt = "Name: " + firstName  + ", Hospital Name: " + hospital_name + ", " + "Requirement: " + blood_unit + "Units";

                                        if(!titletxt.isEmpty() && !msgTxt.isEmpty()){
                                            PushNotification notification = new PushNotification(new NotificationData(titletxt, msgTxt), "/topics/admin");
                                            sendNotification(notification);
                                        }

                                        Intent i = new Intent(BloodRequestActivity.this, UserMainActivity.class);
                                        startActivity(i);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void fetchUserDetails() {
        database.getReference().child("User").child(auth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                firstName = user.getFirst_Name();
                lastName = user.getLast_Name();
                phoneNo = user.getPhoneNo();
                userFirstName.setText(user.getFirst_Name());
                userLastName.setText(user.getLast_Name());
                userContactNo.setText(user.getPhoneNo());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void InitializeWidget() {
        radioGroup1 = findViewById(R.id.radio_grp1);
        radioGroup2 = findViewById(R.id.radio_grp2);
        submitRequest = (Button) findViewById(R.id.blood_request_btn);
        blood_grp_spinner = (Spinner) findViewById(R.id.blood_grp_spinner);
        requiredUpto = findViewById(R.id.required_upto);
        userFirstName = findViewById(R.id.r_user_first_name);
        userLastName = findViewById(R.id.r_user_last_name);
        userContactNo = findViewById(R.id.r_user_contact_no);
        eBloodUnit = findViewById(R.id.blood_unit);
        eHospitalName = findViewById(R.id.hospital_name);
        pdf = findViewById(R.id.pdf);
    }

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
        switch(parent.getId()){
            case(R.id.blood_grp_spinner):
                blood_grp = parent.getItemAtPosition(position).toString();
                break;
            default:
                Toast.makeText(BloodRequestActivity.this, "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, dayOfMonth);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);

        required_upto_date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        requiredUpto.setText(required_upto_date);
    }

    private void sendNotification(PushNotification notification) {

        ApiUtilities.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful()){
                    Toast.makeText(BloodRequestActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BloodRequestActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(BloodRequestActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==35 && resultCode==RESULT_OK){
            if(data.getData()!=null){
                Uri sFile = data.getData();

                storageReference = FirebaseStorage.getInstance().getReference().child("Report Documents").child(sFile.getLastPathSegment());        //to upload the image in storage here we given uid so when user update his profile pic then it will get overridden in storage.... so if we want to create different id for different image uploaded then use push method
                storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(BloodRequestActivity.this,"file Uploaded",Toast.LENGTH_SHORT).show();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                Calendar time = Calendar.getInstance();
//                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
//                                Message model = new Message(SenderId,uri.toString(),new Date().getTime());
                                pdf_url = String.valueOf(uri);
                                pdf.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

            }else {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }

    }
}