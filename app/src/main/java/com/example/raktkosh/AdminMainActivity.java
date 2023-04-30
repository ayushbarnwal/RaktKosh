package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.raktkosh.Adapter.ViewPagerAdminAdapter;
import com.example.raktkosh.Adapter.ViewPagerRequestAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminMainActivity extends AppCompatActivity{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/admin");

        toolbar = findViewById(R.id.tool_bar_3);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.pager1);
        ViewPagerAdminAdapter adapter = new ViewPagerAdminAdapter(getSupportFragmentManager(), "AdminMainActivity");
        viewPager.setAdapter(adapter);

        TabLayout t = findViewById(R.id.tab_layout1);
        t.setupWithViewPager(viewPager);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cancel_request){
            Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.donation_camp){
            Intent i1 = new Intent(AdminMainActivity.this, AdminDonationCampActivity.class);
            startActivity(i1);
        }
        if(item.getItemId() == R.id.admin_logOut){
            SharedPreferences pref = getSharedPreferences("LogIn", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("AdminLogIn", "0");
            editor.apply();

            Intent i = new Intent(AdminMainActivity.this,LogInActivity.class);
            startActivity(i);
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }
}