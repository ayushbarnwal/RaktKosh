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
import android.widget.TextView;
import android.widget.Toast;

import com.example.raktkosh.Adapter.RequestListAdapter;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RaisedRequestFragment extends Fragment {

    RecyclerView request_list_recyclerView;
    RecyclerView completed_request_list_recyclerView;
    RecyclerView cancelled_request_list_recyclerView;
    RequestListAdapter adapter, adapter1, adapter2;
    ArrayList<User> list = new ArrayList<>();
    ArrayList<User> list1 = new ArrayList<>();
    ArrayList<User> list2 = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth auth;
    TextView h;
    String id;

    public RaisedRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_raised_request, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid();

        request_list_recyclerView = view.findViewById(R.id.recycler_view2);
        adapter = new RequestListAdapter(list, getContext(), id, "RaisedRequestFragment");
        request_list_recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        request_list_recyclerView.setLayoutManager(layoutManager);

        cancelled_request_list_recyclerView = view.findViewById(R.id.recycler_view8);
        adapter1 = new RequestListAdapter(list1, getContext(), id, "RaisedRequestFragment");
        cancelled_request_list_recyclerView.setAdapter(adapter1);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        cancelled_request_list_recyclerView.setLayoutManager(layoutManager1);

        completed_request_list_recyclerView = view.findViewById(R.id.recycler_view7);
        adapter2 = new RequestListAdapter(list2, getContext(), id, "RaisedRequestFragment");
        completed_request_list_recyclerView.setAdapter(adapter2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        completed_request_list_recyclerView.setLayoutManager(layoutManager2);

        fetchBloodRequestList();

        return view;
    }

    private void fetchBloodRequestList() {

        database.getReference().child("Blood Request List").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                list.clear();
                list2.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    Log.e("TAG", user.getRequest_status());
                    if(user.getRequest_status().equals("Active")) {
                        Log.e("TAG", user.getRequest_status());
                        list.add(user);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        if(user.getRequest_status().equals("Canceled")){
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