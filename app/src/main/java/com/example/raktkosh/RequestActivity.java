package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.raktkosh.Adapter.ViewPagerRequestAdapter;
import com.google.android.material.tabs.TabLayout;

public class RequestActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        toolbar = findViewById(R.id.tool_bar_2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ViewPager viewPager = findViewById(R.id.pager);
        ViewPagerRequestAdapter adapter = new ViewPagerRequestAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout t = findViewById(R.id.tab_layout);
        t.setupWithViewPager(viewPager);


    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}