package com.example.raktkosh.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.raktkosh.Fragment.UnverifiedYetByAdminFragment;
import com.example.raktkosh.Fragment.VerifiedByAdminFragment;

public class ViewPagerAdminAdapter extends FragmentPagerAdapter {
    String activity;

    public ViewPagerAdminAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdminAdapter(@NonNull FragmentManager fm, String activity) {
        super(fm);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0) return new UnverifiedYetByAdminFragment();
        else return new VerifiedByAdminFragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0) return "UnVerified Requests";
        else return "Verified Requests";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
