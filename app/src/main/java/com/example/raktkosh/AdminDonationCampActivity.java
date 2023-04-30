package com.example.raktkosh;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import com.example.raktkosh.Adapter.DonationCampAdapter;
import com.example.raktkosh.Models.CampDetail;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDonationCampActivity extends AppCompatActivity {

    FloatingActionButton add_camp;
    RecyclerView campRecyclerView;
    ArrayList<CampDetail> list = new ArrayList();
    DonationCampAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donation_camp);

        findViewById(R.id.add_camp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminDonationCampActivity.this, AdminAddCAmpDetail.class);
                i.putExtra("selectedCampAddress", "NULL");
                i.putExtra("latitude", "NULL");
                i.putExtra("longitude", "NULL");
                startActivity(i);
            }
        });

        campRecyclerView = findViewById(R.id.admin_donation_camp_recyclerView);
        adapter = new DonationCampAdapter(list,this, "AdminDonationCampActivity");
        campRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        campRecyclerView.setLayoutManager(layoutManager);

        fetchCampList();


    }

    private void fetchCampList(){
        database.getReference().child("Blood Donation Camp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CampDetail camp = dataSnapshot.getValue(CampDetail.class);
                    camp.setCamp_id(dataSnapshot.getKey());
                    list.add(camp);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}