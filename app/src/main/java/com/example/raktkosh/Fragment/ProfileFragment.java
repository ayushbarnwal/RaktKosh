package com.example.raktkosh.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raktkosh.EditUserProfileActivity;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase database;
    String id;
    ImageView edit_personal_details, edit_contact_details, edit_address_details;
    TextView userName, dateOfBirth, gender, bloodGroup, addressLine, city, state, country, pinCode, emailAddress, contactNumber;


    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        id = auth.getCurrentUser().getUid().toString();

        widgetInitialize(view);

        fetchUserDetail();

        edit_personal_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditUserProfileActivity.class);
                startActivity(i);
            }
        });
        edit_contact_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditUserProfileActivity.class);
                startActivity(i);
            }
        });
        edit_address_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditUserProfileActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    private void fetchUserDetail() {
        database.getReference().child("User").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user.setUserId(id);
                userName.setText(user.getFirst_Name());
                dateOfBirth.setText(user.getDate_Of_Birth());
                gender.setText(user.getGender());
                bloodGroup.setText(user.getBlood_Group());
                addressLine.setText(user.getPostal_Address());
                emailAddress.setText(user.getEmail_Address());
                contactNumber.setText(user.getPhoneNo());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void widgetInitialize(View view) {
        edit_personal_details = view.findViewById(R.id.edit_personal_detail);
        edit_address_details = view.findViewById(R.id.edit_address_detail);
        edit_contact_details = view.findViewById(R.id.edit_contact_detail);

        userName = view.findViewById(R.id.user_name);
        dateOfBirth = view.findViewById(R.id.user_dob);
        gender = view.findViewById(R.id.user_gender);
        bloodGroup = view.findViewById(R.id.user_blood_grp);
        addressLine = view.findViewById(R.id.user_address_line);
        city = view.findViewById(R.id.user_city);
        country = view.findViewById(R.id.user_country);
        state = view.findViewById(R.id.user_state);
        pinCode = view.findViewById(R.id.user_pin_code);

        emailAddress = view.findViewById(R.id.user_email_address);
        contactNumber = view.findViewById(R.id.user_mobile_no);
    }
}