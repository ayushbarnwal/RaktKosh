package com.example.raktkosh.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.raktkosh.Fragment.DonatedRequestFragment;
import com.example.raktkosh.Fragment.RaisedRequestFragment;
import com.example.raktkosh.Fragment.UnverifiedYetByAdminFragment;
import com.example.raktkosh.Fragment.VerifiedByAdminFragment;

public class ViewPagerRequestAdapter extends FragmentPagerAdapter {

    String activity;
    public ViewPagerRequestAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ViewPagerRequestAdapter(@NonNull FragmentManager fm, String activity) {
        super(fm);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
//        if(activity.equals("AdminMainActivity")){
//            if(position==0) return new UnverifiedYetByAdminFragment();
//            else return new VerifiedByAdminFragment();
//        }
        if(position==0) return new RaisedRequestFragment();
        else return new DonatedRequestFragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        if(activity.equals("AdminMainActivity")){
//            if(position==0) return "UnVerified Requests";
//            else return "Verified Requests";
//        }
        if(position==0) return "Raised Request";
        else return "Donated Request";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
