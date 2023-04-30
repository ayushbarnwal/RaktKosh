package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.raktkosh.Fragment.CampFragment;
import com.example.raktkosh.Fragment.ProfileFragment;
import com.example.raktkosh.Fragment.RequestListFragment;
import com.example.raktkosh.Fragment.UserMapMainFragment;
import com.example.raktkosh.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class UserMainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
//implements NavigationBarView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    FirebaseAuth auth;
    FirebaseDatabase database;

    FloatingActionButton bloodRequest;
    ImageView headerProfileImage;
    TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String id = auth.getCurrentUser().getUid();

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + id);
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/user");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("Blood Request List");

        bloodRequest = findViewById(R.id.blood_request);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        setSupportActionBar(toolbar);

        View header = navigationView.getHeaderView(0);
        user_name = (TextView) header.findViewById(R.id.navigation_user_name);
        headerProfileImage = (ImageView) header.findViewById(R.id.profile_picture_navigation);

        database.getReference().child("User").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("TAG", "200");
                User user = snapshot.getValue(User.class);
                String mname = user.getFirst_Name();
                user_name.setText(mname);
                Glide.with(UserMainActivity.this).load(user.getProfilePic()).placeholder(R.drawable.user).into(headerProfileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "201");

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getFragmentManager().findFragmentById(androidx.appcompat.R.id.action_bar_container) == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new RequestListFragment()).commit();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.navigation_logOut){

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(UserMainActivity.this);
                    builder1.setMessage("Are you Sure you want to Log Out");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    auth.signOut();

                                    SharedPreferences pref = getSharedPreferences("LogIn", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("UserLogIn", "0");
                                    editor.apply();

                                    Intent i = new Intent(UserMainActivity.this,LogInActivity.class);
                                    startActivity(i);
                                    finishAffinity();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
                else if(id == R.id.navigation_profile){
                    getSupportFragmentManager().beginTransaction().add(R.id.flFragment,new ProfileFragment()).commit();
                    bloodRequest.setVisibility(View.GONE);
                }
                else if(id == R.id.navigation_raised_request || id == R.id.navigation_donation_request){
                    Intent i = new Intent(UserMainActivity.this, RequestActivity.class);
                    startActivity(i);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(this);
        bloodRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserMainActivity.this, BloodRequestActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

                case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new ProfileFragment()).commit();
                    bloodRequest.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("User Profile");
                    return true;
            case R.id.map_user_main:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new UserMapMainFragment()).commit();
                bloodRequest.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                toolbar.setTitle("Map Search");
                return true;
                case R.id.request_list:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new RequestListFragment()).commit();
                    bloodRequest.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Blood Request List");
                    return true;
                case R.id.camp_list:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new CampFragment()).commit();
                    bloodRequest.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Blood Donation Camp");
                    return true;

        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}