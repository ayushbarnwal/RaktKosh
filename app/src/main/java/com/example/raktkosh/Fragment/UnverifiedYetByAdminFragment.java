package com.example.raktkosh.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raktkosh.Adapter.RequestListAdapter;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UnverifiedYetByAdminFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<User> list = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RequestListAdapter adapter;
    public UnverifiedYetByAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unverified_yet_by_admin, container, false);

        recyclerView = view.findViewById(R.id.recycler_view6);
        adapter = new RequestListAdapter( list, getActivity(), "UnVerifiedByAdminActivity");
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        fetchUserList();

        return view;
    }

    private void fetchUserList() {

        database.getReference().child("Blood Request List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    database.getReference().child("Blood Request List").child(key).orderByChild("hospitalName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                                User user = dataSnapshot1.getValue(User.class);
                                if(user.getGotVerified().equals("No")){
                                    list.add(user);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}