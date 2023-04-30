package com.example.raktkosh.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raktkosh.Adapter.DonationCampAdapter;
import com.example.raktkosh.Models.CampDetail;
import com.example.raktkosh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CampFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase database;
    String id;
    ArrayList<CampDetail> list = new ArrayList();
    RecyclerView campRecyclerView;
    DonationCampAdapter adapter;
    public CampFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camp, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid().toString();

        campRecyclerView = view.findViewById(R.id.camp_user_recycler_view);
        adapter = new DonationCampAdapter(list,getContext(), "CampFragment");
        campRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        campRecyclerView.setLayoutManager(layoutManager);

        fetchCampList();

        return view;
    }

    private void fetchCampList(){
        database.getReference().child("Blood Donation Camp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CampDetail camp = dataSnapshot.getValue(CampDetail.class);
                    camp.setCamp_id(dataSnapshot.getKey());
                    if(Long.parseLong(camp.getReminder_time()) >= System.currentTimeMillis()){
                        list.add(camp);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}