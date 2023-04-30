package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raktkosh.Adapter.RequestListAdapter;
import com.example.raktkosh.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestExploreActivity extends AppCompatActivity {

    TextView type_selected, blood_grp, user_name, blood_unit, hospital_address, patient_situation, have_replacement, hospital_type, contact_number, status, hospital_name, got_verified;
    TextView textView1, unitFulfilled, userAcceptedRequest;
    ImageView imageView;
    RecyclerView donorRecyclerView;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String id, adapter_id;
    RequestListAdapter adapter;
    ArrayList<User> list = new ArrayList<>();
    String document_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_explore);

        Toolbar tb = findViewById(R.id.tool_bar_5);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid();

        initializeWidget();

        adapter = new RequestListAdapter(list, RequestExploreActivity.this, id, "RequestExploreActivity");
        donorRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(RequestExploreActivity.this);
        donorRecyclerView.setLayoutManager(layoutManager);

        fetchdata();

        fetchDonorsList();

        findViewById(R.id.document).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG" , document_url);
                Intent i = new Intent(RequestExploreActivity.this, PdfViewerActivity.class);
                i.putExtra("pdf_url", document_url);
                startActivity(i);
            }
        });
    }

    private void fetchDonorsList() {

        database.getReference().child("Blood Request List").child(id).child(adapter_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                //Log.e("TAG", "j "+ u.getUserId());
                database.getReference().child("Blood Request List").child(id).child(adapter_id).child("Donors")
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
                                                user1.setBloodUnit(u.getBloodUnit());
                                                user1.setUnit_fulfilled(u.getUnit_fulfilled());
                                                user1.setUserId(donorId);
                                                user1.setRequest_id(adapter_id);
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
        hospital_name.setText(getIntent().getExtras().getString("hospital_address"));
        got_verified.setText(getIntent().getExtras().getString("gotVerified"));
        adapter_id = getIntent().getExtras().getString("adapter_id");
        unitFulfilled.setText(getIntent().getExtras().getString("unit_fulfilled"));
        status.setText(getIntent().getExtras().getString("request_status"));
        document_url = getIntent().getExtras().getString("pdf_url");
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
        imageView = findViewById(R.id.imageView3);
        donorRecyclerView = findViewById(R.id.recycler_view11);
        textView1 = findViewById(R.id.textView5);
        unitFulfilled = findViewById(R.id.unit_fulfilled);
        userAcceptedRequest = findViewById(R.id.user_accepted_request);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}