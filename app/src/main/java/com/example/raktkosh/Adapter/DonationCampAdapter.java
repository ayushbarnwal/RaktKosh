package com.example.raktkosh.Adapter;


import static android.app.AlarmManager.*;
import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.raktkosh.CampLocateMapActivity;
import com.example.raktkosh.CampReminder.MyReciever;
import com.example.raktkosh.Models.CampDetail;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DonationCampAdapter extends RecyclerView.Adapter<DonationCampAdapter.ViewHolder> {

    Context context;
    ArrayList<CampDetail> list;
    String location;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public DonationCampAdapter(ArrayList<CampDetail> list, Context context, String location){
        this.list = list;
        this.context = context;
        this.location = location;
    }

    @NonNull
    @Override
    public DonationCampAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donation_camp_layout, parent, false);
        return new DonationCampAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CampDetail camp = list.get(position);
        holder.camp_date.setText(camp.getCamp_date());
        holder.camp_month.setText(camp.getCamp_month());
        holder.camp_year.setText(camp.getCamp_year());
        holder.hospital_name.setText(camp.getCamp_address());

        holder.camp_time.setText(camp.getCamp_start_time() + "-" + camp.getCamp_end_time());

        if(location.equals("AdminDonationCampActivity")){
            holder.camp_layout_first_button.setText("Cancel Camp");
            holder.camp_layout_first_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Cancel the Camp?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.getReference().child("Blood Donation Camp").child(camp.getCamp_id()).setValue(null);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert11 = builder.create();
                    alert11.show();

                }
            });
        }

        if(location.equals("CampFragment")){
            if(camp.getSet_reminder().equals("yes")){
                holder.camp_layout_first_button.setText("Reminder Set");
                holder.camp_layout_first_button.setTextColor(Color.WHITE);
                holder.camp_layout_first_button.setBackgroundColor(Color.parseColor("#B00020"));
            }
            holder.camp_layout_first_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.camp_layout_first_button.getText().equals("Remind Me")){
                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("set_reminder", "yes");
                        database.getReference().child("Blood Donation Camp").child(camp.getCamp_id()).updateChildren(obj);
                        holder.camp_layout_first_button.setText("Reminder Set");
                        holder.camp_layout_first_button.setTextColor(Color.parseColor("#B00020"));
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        String time = camp.getReminder_time();
                        long reminderTime = Long.parseLong(time);
                        if(reminderTime >= System.currentTimeMillis()){
                            Log.e("TAG", "n3");
                            Intent i = new Intent(context, MyReciever.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
                        }
                    }else{
                        Toast.makeText(context, "Reminder Already Set", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            holder.camp_layout_second_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, CampLocateMapActivity.class);
                    i.putExtra("latitude", camp.getLatitude());
                    i.putExtra("longitude", camp.getLongitude());
                    i.putExtra("address", camp.getCamp_address());
                    context.startActivity(i);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView hospital_name, camp_date,camp_month, camp_year, camp_time, camp_layout_first_button, camp_layout_second_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hospital_name = itemView.findViewById(R.id.camp_hospital_name);
            camp_date = itemView.findViewById(R.id.camp_date);
            camp_month = itemView.findViewById(R.id.camp_month);
            camp_year = itemView.findViewById(R.id.camp_year);
            camp_time = itemView.findViewById(R.id.camp_time);
            camp_layout_first_button = itemView.findViewById(R.id.camp_layout_first_button);
            camp_layout_second_button = itemView.findViewById(R.id.camp_layout_second_button);
        }
    }
}
