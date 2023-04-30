package com.example.raktkosh.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.raktkosh.Adapter.RequestListAdapter;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


public class RequestListFragment extends Fragment {

    RecyclerView request_list_recyclerView;
    RequestListAdapter adapter;
    ArrayList<User> list = new ArrayList<>();

    FirebaseDatabase database;
    FirebaseAuth auth;
    String id;
    User user;
    private MaterialSearchBar materialSearchBar;

    public RequestListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid();

        materialSearchBar = view.findViewById(R.id.searchBar1);

        request_list_recyclerView = view.findViewById(R.id.request_list_recycler_view);
        adapter = new RequestListAdapter(list, getContext(), id, "RequestListFragment");
        request_list_recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        request_list_recyclerView.setLayoutManager(layoutManager);

        fetchBloodRequestList();

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void fetchBloodRequestList() {

        database.getReference().child("Blood Request List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        String root = dataSnapshot.getKey();
                        if(!root.equals(id)){
                            Set<String> keySet = map.keySet();
                            for(String key: keySet){
                                database.getReference().child("Blood Request List")
                                        .child(root).child(key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                user = snapshot.getValue(User.class);
                                                if(user.getGotVerified().equals("Yes") && user.getRequest_status().equals("Active")){
                                                    list.add(user);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {}
                                        });
                            }
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void filter(String text){
        ArrayList<User> filterList = new ArrayList<>();
        for(User user: list){
            if(user.getHospitalName().toLowerCase().contains(text.toLowerCase())){
                filterList.add(user);
            }
        }
        adapter.Filteredlist(filterList);

    }
}