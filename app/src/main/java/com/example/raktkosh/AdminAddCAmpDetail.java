package com.example.raktkosh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.raktkosh.Models.CampDetail;
import com.example.raktkosh.Models.DatePickerFragment;
import com.example.raktkosh.Notification.ApiUtilities;
import com.example.raktkosh.Notification.NotificationData;
import com.example.raktkosh.Notification.PushNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddCAmpDetail extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextView notify_user, camp_date, camp_start_time, camp_end_time, camp_address;
    ImageView edit_location;
    String full_date, start_time, end_time, cdate, cmonth, cyear;
    FirebaseDatabase database;
    String reminder_time, campAddress, latitude, longitude;
    String day, day2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_camp_detail);

        initializeWidget();

        database = FirebaseDatabase.getInstance();
        campAddress = getIntent().getStringExtra("selectedCampAddress").toString();
        latitude = getIntent().getStringExtra("latitude").toString();
        longitude = getIntent().getStringExtra("longitude").toString();
        Toast.makeText(this, campAddress, Toast.LENGTH_SHORT).show();
        camp_address.setText(campAddress);

        camp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date Picker");
            }
        });

        camp_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AdminAddCAmpDetail.this, androidx.appcompat.R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c1.set(Calendar.MINUTE, minute);

                        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm:ss z");
                        day2 = format1.format(c1.getTime());
                        String reminder = day + " " + day2;
                        Log.e("TAG", "time:  " + reminder);

                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z");
                        try {
                            Date mDate = sdf.parse(reminder);
                            long timeInMilliseconds = mDate.getTime();
                            Log.e("TAG", "Date in milli :: " + timeInMilliseconds);
                            reminder_time = String.valueOf(timeInMilliseconds);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                        start_time = format.format(c1.getTime());
                        camp_start_time.setText(start_time);
                    }
                },hours, min, false);
                timePickerDialog.show();

            }
        });

        camp_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AdminAddCAmpDetail.this, androidx.appcompat.R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c1.set(Calendar.MINUTE, minute);
                        c1.setTimeZone(TimeZone.getDefault());

                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                        end_time = format.format(c1.getTime());
                        camp_end_time.setText(end_time);
                    }
                },hours, min, false);
                timePickerDialog.show();
            }
        });

        findViewById(R.id.edit_camp_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(AdminAddCAmpDetail.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(AdminAddCAmpDetail.this, AddressMapActivity.class));
                    finish();
                    return;
                }else{

                    Dexter.withContext(AdminAddCAmpDetail.this)
                            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    startActivity(new Intent(AdminAddCAmpDetail.this, AddressMapActivity.class));
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    if(permissionDeniedResponse.isPermanentlyDenied()){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddCAmpDetail.this);
                                        builder.setTitle("Permission Denied")
                                                .setMessage("Permission to access device location is Permanently Denied...you need to go setting to allow the permission")
                                                .setNegativeButton("Cancel", null)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent();
                                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                    }
                                                }).show();
                                    }else{
                                        Toast.makeText(AdminAddCAmpDetail.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();

                }

            }
        });

        notify_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cdate!=null && cmonth!=null && cyear!=null && start_time!=null && end_time!=null && !camp_address.equals("NULL")){

                }
                CampDetail camp = new CampDetail(cdate, cmonth, cyear, start_time, end_time, campAddress, reminder_time, "No", latitude, longitude);
                database.getReference().child("Blood Donation Camp").push().setValue(camp).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String titletxt = "Blood Donation Camp";
                        String msgTxt = campAddress;
                        if(!titletxt.isEmpty() && !msgTxt.isEmpty()){
                            PushNotification notification = new PushNotification(new NotificationData(titletxt, msgTxt), "/topics/user");
                            sendNotification(notification);
                        }

                        Intent i = new Intent(AdminAddCAmpDetail.this, AdminDonationCampActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
            }
        });

    }

    private void initializeWidget() {
        notify_user = findViewById(R.id.notify_blood_camp_admin);
        camp_date = findViewById(R.id.blood_camp_date_admin);
        camp_address = findViewById(R.id.blood_camp_address_admin);
        camp_start_time = findViewById(R.id.blood_camp_start_time_admin);
        camp_end_time = findViewById(R.id.blood_camp_end_time_admin);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, dayOfMonth);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        cdate = dateFormat.format(c.getTime());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM");
        cmonth = dateFormat1.format(c.getTime());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy");
        cyear = dateFormat2.format(c.getTime());
        SimpleDateFormat dateFormat4 = new SimpleDateFormat("EEE MMM dd yyyy");
        day = dateFormat4.format(c.getTime());

        full_date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        camp_date.setText(full_date);
    }

    private void sendNotification(PushNotification notification) {

        ApiUtilities.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful()){
                    Toast.makeText(AdminAddCAmpDetail.this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AdminAddCAmpDetail.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(AdminAddCAmpDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}