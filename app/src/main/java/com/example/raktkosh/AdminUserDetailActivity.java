package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raktkosh.Adapter.RequestListAdapter;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.Notification.ApiUtilities;
import com.example.raktkosh.Notification.NotificationData;
import com.example.raktkosh.Notification.PushNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserDetailActivity extends AppCompatActivity {

    TextView type_selected, blood_grp, user_name, blood_unit, hospital_address, patient_situation, have_replacement, hospital_type, contact_number, status, hospital_name, got_verified, sos_requested, unit_fulfilled;
    String user_id, adapter_id, mobNo;
    TextView textView1;
    ImageView imageView;
    ProgressBar pb;
    RecyclerView donorRecyclerView;
    Switch makeVerified;
    ConstraintLayout constraintLayout;
    CardView cardView;
    RequestListAdapter adapter;
    ArrayList<User> list = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String hospital_Name;
    String url1;
    TextView markEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        hospital_Name = getIntent().getExtras().getString("hospital_address");

        initializeWidget();
        fetchdata();
        pb.getIndeterminateDrawable().setColorFilter(0xB00020, android.graphics.PorterDuff.Mode.MULTIPLY);

        adapter = new RequestListAdapter(list, AdminUserDetailActivity.this, "AdminUserDetailActivity");
        donorRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AdminUserDetailActivity.this);
        donorRecyclerView.setLayoutManager(layoutManager);

        makeVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "b1");
                if(makeVerified.isChecked()){
                    Log.e("TAG", "b2");
                    makeVerified.setText("");
                    pb.setVisibility(View.VISIBLE);
                }else{
                    Log.e("TAG", "b3");
                    makeVerified.setText("");
                    pb.setVisibility(View.VISIBLE);
                }
                Log.e("TAG", "b4");
                setSwitchState();
            }

        });

        if(makeVerified.isChecked()) fetchDonorsList();

        database.getReference().child("Blood Request List").child(user_id).child(adapter_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                url1 = user.getDocument_pdf();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.pdf2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminUserDetailActivity.this, PdfViewerActivity.class);
                i.putExtra("pdf_url", url1);
                startActivity(i);
            }
        });

        markEmergency = findViewById(R.id.mark_as_emergency);
        if(getIntent().getExtras().getString("SosRequested").equals("Yes") && getIntent().getExtras().getString("patient_situation").equals("Extreme")){
            markEmergency.setBackgroundColor(Color.parseColor("#B00020"));
            markEmergency.setTextColor(Color.WHITE);
            markEmergency.setText("Marked Emergency");
        }


        markEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent().getExtras().getString("SosRequested").equals("Yes") && getIntent().getExtras().getString("patient_situation").equals("Normal")){
                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("patient_situation", "Extreme");
                    database.getReference().child("Blood Request List").child(user_id).child(adapter_id).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            markEmergency.setBackgroundColor(Color.parseColor("#B00020"));
                            markEmergency.setTextColor(Color.WHITE);
                            patient_situation.setText("Extreme");
                            markEmergency.setText("Marked Emergency");
                        }
                    });
                }
            }
        });
    }

    private void fetchDonorsList() {
        Toast.makeText(this, user_id + " " + adapter_id, Toast.LENGTH_SHORT).show();
        database.getReference().child("Blood Request List").child(user_id).child(adapter_id).child("Donors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                User user = dataSnapshot.getValue(User.class);
                                String donorId = dataSnapshot.getKey();
                                database.getReference().child("User").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        list.clear();
                                        User user1 = snapshot.getValue(User.class);
                                        user1.setDonateUnit(user.getDonateUnit());
                                        user1.setRequest_id(donorId);
                                        if(!user1.getFirst_Name().equals(null)){
                                            imageView.setVisibility(View.GONE);
                                            textView1.setVisibility(View.GONE);
                                        }
                                        list.add(user1);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

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
    private void setSwitchState() {
        Log.e("TAG", "b5");

        database.getReference().child("Blood Request List").child(user_id).child(adapter_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("TAG", "b6");
                if(makeVerified.isChecked()){
                    Log.e("TAG", "b7");
                    snapshot.getRef().child("gotVerified").setValue("Yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("TAG", "b8");
                            pb.setVisibility(View.GONE);
                            makeVerified.setText("Verified");
                            cardView.setVisibility(View.VISIBLE);
                            constraintLayout.setVisibility(View.VISIBLE);
//                            SmsManager smsManager = SmsManager.getDefault();
//                            smsManager.sendTextMessage("+91" + mobNo,null, "got Verified", null, null);
                            Log.e("TAG", "b9");
                            String titletxt = "Request Verified";
                            String msgTxt = "Blood Request Raised for" + hospital_Name + "has been verified by Admin";

                            if(!titletxt.isEmpty() && !msgTxt.isEmpty()){
                                Log.e("TAG", "b10");
                                PushNotification notification = new PushNotification(new NotificationData(titletxt, msgTxt), "/topics/" + user_id);
                                sendNotification(notification);
                                Log.e("TAG", "b11");
                            }
                        }
                    });
                }
                else{
                    Log.e("TAG", "b12");
                    snapshot.getRef().child("gotVerified").setValue("No").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("TAG", "b13");
                            makeVerified.setText("Not Verified Yet");
                            pb.setVisibility(View.GONE);
//                            SmsManager smsManager = SmsManager.getDefault();
//                            smsManager.sendTextMessage("+91" + mobNo,null, "got DisQualified", null, null);
                            Log.e("TAG", "b14");
                            String titletxt = "Request Declined";
                            String msgTxt = "Blood Request Raised for" + hospital_Name + "has been declined by Admin";

                            if(!titletxt.isEmpty() && !msgTxt.isEmpty()){
                                Log.e("TAG", "b15");
                                PushNotification notification = new PushNotification(new NotificationData(titletxt, msgTxt), "/topics/" + user_id);
                                sendNotification(notification);
                                Log.e("TAG", "b16");
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchdata() {
        type_selected.setText(getIntent().getExtras().getString("type_selected"));
        blood_grp.setText(getIntent().getExtras().getString("blood_grp"));
        user_name.setText(getIntent().getExtras().getString("user_name"));
        blood_unit.setText("Requirement: " + getIntent().getExtras().getString("blood_unit") + " Units");
        hospital_address.setText(getIntent().getExtras().getString("hospital_address"));
        have_replacement.setText(getIntent().getExtras().getString("haveReplacement"));
        contact_number.setText(getIntent().getExtras().getString("contactNo"));
        mobNo = getIntent().getExtras().getString("contactNo");
        hospital_name.setText(getIntent().getExtras().getString("hospital_address"));
        got_verified.setText(getIntent().getExtras().getString("gotVerified"));
        adapter_id = getIntent().getExtras().getString("adapter_id");
        user_id = getIntent().getExtras().getString("userId");
        patient_situation.setText(getIntent().getExtras().getString("patient_situation"));
        status.setText(getIntent().getExtras().getString("status"));
        sos_requested.setText(getIntent().getExtras().getString("SosRequested"));
        unit_fulfilled.setText(getIntent().getExtras().getString("unit_fulfilled"));
        String isVerify = getIntent().getExtras().getString("isVerify");
        if(isVerify.equals("No")){
            makeVerified.setChecked(false);
            makeVerified.setText("Not Verified yet");
            constraintLayout.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }
        else{
            makeVerified.setChecked(true);
            makeVerified.setText("verified");
        }
    }

    private void initializeWidget() {
        type_selected = findViewById(R.id.mtype_selected);
        blood_grp = findViewById(R.id.mblood_grp);
        user_name = findViewById(R.id.muser_name);
        blood_unit = findViewById(R.id.mblood_unit);
        hospital_address = findViewById(R.id.mhosital_name);
        patient_situation = findViewById(R.id.mpatient_situation);
        have_replacement = findViewById(R.id.mhas_Replacement);
        hospital_type = findViewById(R.id.mhospital_type);
        contact_number = findViewById(R.id.mcontact_number);
        status = findViewById(R.id.mstatus);
        hospital_name = findViewById(R.id.mhospital_name);
        got_verified = findViewById(R.id.got_verified);
        makeVerified = findViewById(R.id.isVerified);
        imageView = findViewById(R.id.imageView3);
        donorRecyclerView = findViewById(R.id.recycler_view12);
        textView1 = findViewById(R.id.textView5);
        constraintLayout = findViewById(R.id.constraint_layout3);
        cardView = findViewById(R.id.card_view2);
        pb = findViewById(R.id.progressBar5);
        pb.setVisibility(View.GONE);
        sos_requested = findViewById(R.id.sos1);
        unit_fulfilled = findViewById(R.id.unit_fulfilled1);
    }

    private void sendNotification(PushNotification notification) {

        ApiUtilities.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful()){
                    Toast.makeText(AdminUserDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AdminUserDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(AdminUserDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}