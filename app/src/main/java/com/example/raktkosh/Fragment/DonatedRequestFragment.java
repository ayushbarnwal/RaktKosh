package com.example.raktkosh.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.raktkosh.Adapter.RequestListAdapter;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DonatedRequestFragment extends Fragment {

    RecyclerView sentRequestRecyclerView3, donationCompletedRecyclerView4, canceledDonatedRecyclerView5;
    RequestListAdapter adapter1;
    RequestListAdapter adapter2;
    RequestListAdapter adapter3;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<User> list1 = new ArrayList<>();
    ArrayList<User> list2 = new ArrayList<>();
    ArrayList<User> list3 = new ArrayList<>();
    String id;
    public DonatedRequestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donated_request, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid();

        sentRequestRecyclerView3 = view.findViewById(R.id.recycler_view3);
        donationCompletedRecyclerView4 = view.findViewById(R.id.recycler_view4);
        canceledDonatedRecyclerView5 = view.findViewById(R.id.recycler_view5);

        adapter1 = new RequestListAdapter(list1, getContext(), id, "DonatedRequestFragment");
        sentRequestRecyclerView3.setAdapter(adapter1);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        sentRequestRecyclerView3.setLayoutManager(layoutManager1);

        adapter2 = new RequestListAdapter(list2, getContext(), id, "DonatedRequestFragment");
        donationCompletedRecyclerView4.setAdapter(adapter2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        donationCompletedRecyclerView4.setLayoutManager(layoutManager2);

        adapter3 = new RequestListAdapter(list3, getContext(), id, "DonatedRequestFragment");
        canceledDonatedRecyclerView5.setAdapter(adapter3);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        canceledDonatedRecyclerView5.setLayoutManager(layoutManager3);

        fetchDonatedRequestList();

        return view;
    }

    private void fetchDonatedRequestList() {

        database.getReference().child("User").child(id).child("Donee").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String doneeId = dataSnapshot.getKey();
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Set<String> keySet = map.keySet();
                    list1.clear();
                    list2.clear();
                    list3.clear();
                    for(String key: keySet){
                        database.getReference().child("Blood Request List").child(doneeId).child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                User u = snapshot.child("Donors").child(id).getValue(User.class);
                                user.setDonateUnit(u.getDonateUnit());
                                user.setDonation_request(u.getDonation_request());
                                user.setHasDonated(u.getHasDonated());
                                if(u.getDonation_request().equals("Rejected")){
                                    list3.add(user);
                                    adapter3.notifyDataSetChanged();
                                }else{
                                    if(u.getRequest_status().equals("Active") | u.getRequest_status().equals("completed")){
                                        if(u.getHasDonated().equals("No")){
                                            list1.add(user);
                                            adapter1.notifyDataSetChanged();
                                        }else{
                                            list2.add(user);
                                            adapter2.notifyDataSetChanged();
                                        }
                                    }
                                }
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
}