package com.example.raktkosh.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.raktkosh.AdminUserDetailActivity;
import com.example.raktkosh.LogInActivity;
import com.example.raktkosh.Models.AlerDialogBloodDonate;
import com.example.raktkosh.Models.User;
import com.example.raktkosh.Notification.ApiUtilities;
import com.example.raktkosh.Notification.NotificationData;
import com.example.raktkosh.Notification.PushNotification;
import com.example.raktkosh.R;
import com.example.raktkosh.RequestExploreActivity;
import com.example.raktkosh.UserChatActivity;
import com.example.raktkosh.UserMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {


    ArrayList<User> list;
    Context context;
    String id, blood_donate, location;
    EditText eblood_unit_donate;
    TextView confirm, cancel;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    //User user;



    public RequestListAdapter(ArrayList<User> list, Context context, String id, String location){
        this.list = list;
        this.context = context;
        this.id = id;
        this.location = location;
    }

    public RequestListAdapter(ArrayList<User> list, Context context, String location){
        this.list = list;
        this.context = context;
        this.location = location;
    }


    @NonNull
    @Override
    public RequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_blood_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RequestListAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.muser_name.setText(user.getFirst_Name()+" "+user.getLast_Name());
        holder.mblood_grp.setText(user.getBlood_Group());
        holder.mtype_selected.setText(user.getTypeRequested());
        holder.mblood_unit.setText("Requirement: " + user.getBloodUnit() + " Units");
        holder.mhospital_address.setText(user.getHospitalName());
        holder.patient_situation.setText(user.getPatient_situation());

        if(location.equals("RequestListFragment")){
            holder.exploreRequest.setVisibility(View.GONE);
            holder.imageView1.setVisibility(View.GONE);
            holder.imageMenu.setVisibility(View.GONE);
            holder.acceptBloodRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Do you Really want to Accept Request");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int id) {
                                    Dialog dialog = new Dialog(context);
                                    database.getReference().child("User").child(auth.getCurrentUser().getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    User user2 = snapshot.getValue(User.class);
                                                    if(user2.getBlood_Group().equals(user.getBlood_Group())){
                                                        //dialog = new Dialog(context);
                                                        dialog.setContentView(R.layout.donate_blood_alert_dialog);
                                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                        dialog.setCancelable(false);
                                                        //dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                                                        confirm = dialog.findViewById(R.id.confirm);
                                                        cancel = dialog.findViewById(R.id.cancel);
                                                        eblood_unit_donate = dialog.findViewById(R.id.blood_unit_donate);

                                                        confirm.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String s = eblood_unit_donate.getText().toString();
                                                                if(s.length() != 0) {
                                                                    HashMap<String , Object> obj = new HashMap<>();
                                                                    obj.put("DonateUnit", s);
                                                                    obj.put("request_status", "Active");
                                                                    obj.put("hasDonated", "No");
                                                                    obj.put("donation_request", "accepted");
                                                                    database.getReference().child("Blood Request List")
                                                                            .child(user.getUserId()).child(user.getRequest_id())
                                                                            .child("Donors").child(auth.getCurrentUser().getUid())
                                                                            .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    database.getReference().child("User").child(auth.getCurrentUser().getUid())
                                                                                            .child("Donee").child(user.getUserId()).child(user.getRequest_id())
                                                                                            .setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {

                                                                                                    String titletxt = "Request Accepted";
                                                                                                    String msgTxt = "Blood Request Raised for has been accepted by One Donor";

                                                                                                    if(!titletxt.isEmpty() && !msgTxt.isEmpty()){
                                                                                                        PushNotification notification = new PushNotification(new NotificationData(titletxt, msgTxt), "/topics/" + user.getUserId());
                                                                                                        sendNotification(notification);
                                                                                                    }
                                                                                                    dialog.dismiss();
                                                                                                }
                                                                                            });
                                                                                }
                                                                            });
                                                                }else{
                                                                    Toast.makeText(context, "Please Enter Blood Unit", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                                        cancel.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        dialog.show();
                                                    }else{
                                                        Toast.makeText(context, "Blood Group is Not Matching", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int id) {
                                    dialog1.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        }

        if(location.equals("RaisedRequestFragment")){
            holder.shareBloodRequest.setVisibility(View.GONE);
            holder.acceptBloodRequest.setVisibility(View.GONE);
            holder.imageView1.setVisibility(View.GONE);
            holder.mhospital_address.setTextColor(Color.parseColor("#3EB489"));

            holder.exploreRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG", "Unit Fullfiled : " + user.getUnit_fulfilled());
                    Intent i = new Intent(context, RequestExploreActivity.class);
                    i.putExtra("user_name", user.getFirst_Name() + user.getLast_Name());
                    i.putExtra("blood_grp", user.getBlood_Group());
                    i.putExtra("type_selected", user.getTypeRequested());
                    i.putExtra("blood_unit", user.getBloodUnit());
                    i.putExtra("hospital_address", user.getHospitalName());
                    i.putExtra("haveReplacement", user.getHaveReplacement());
                    i.putExtra("contactNo", user.getPhoneNo());
                    i.putExtra("gotVerified", user.getGotVerified());
                    i.putExtra("adapter_id", user.getRequest_id());
                    i.putExtra("unit_fulfilled", user.getUnit_fulfilled());
                    i.putExtra("request_status", user.getRequest_status());
                    i.putExtra("pdf_url", user.getDocument_pdf());
                    context.startActivity(i);
                }
            });

            if(user.getRequest_status().equals("Active")){
                holder.imageMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(context, holder.imageMenu);
                        popup.inflate(R.menu.admin_menu6);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.cancel_request:
                                        cancelRequest(user);
                                        return true;
                                    case R.id.sos2:
                                        sosRequest(user);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }
            else if(user.getRequest_status().equals("Completed")){
                holder.imageMenu.setVisibility(View.GONE);
            }
            else{
                holder.imageMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(context, holder.imageMenu);
                        popup.inflate(R.menu.admin_menu2);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.activate_request:
                                        activateRequest(user);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }

        }

        if(location.equals("RequestExploreActivity")){
            holder.mtype_selected.setVisibility(View.INVISIBLE);
            holder.mhospital_address.setText(user.getPostal_Address());
            holder.acceptBloodRequest.setVisibility(View.GONE);
            holder.exploreRequest.setVisibility(View.GONE);
            holder.shareBloodRequest.setVisibility(View.GONE);
            holder.patient_situation.setText(user.getEmail_Address());
            holder.patient_situation.setTextSize(14);
            holder.constraintLayout1.setVisibility(View.GONE);
            holder.mblood_unit.setText("Blood Unit want to Donate: " + user.getDonateUnit());
            holder.mblood_grp.setVisibility(View.GONE);
            holder.mblood_unit.setTextColor(Color.parseColor("#3EB489"));
            //holder.imageView1.setVisibility(View.GONE);
            Glide.with(context).load(user.getProfilePic()).placeholder(R.drawable.user).into(holder.imageView1);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserChatActivity.class);
                    intent.putExtra("DonorId", user.getUserId());
                    intent.putExtra("DonorName", user.getFirst_Name());
                    intent.putExtra("DonorProfilePic", user.getProfilePic());
                    context.startActivity(intent);
                }
            });

            holder.imageMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.imageMenu);
                    popup.inflate(R.menu.admin_menu4);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.accept_request:
                                    acceptBloodRequest(user);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
        }

        if(location.equals("DonatedRequestFragment")){
            holder.imageView1.setVisibility(View.GONE);
            holder.patient_situation.setTextColor(Color.parseColor("#3EB489"));
            holder.mblood_unit.setText("Requirement: " + user.getBloodUnit());
            holder.patient_situation.setText("Donate Unit: " + user.getDonateUnit());
            holder.acceptBloodRequest.setVisibility(View.GONE);
            holder.exploreRequest.setVisibility(View.GONE);
            holder.shareBloodRequest.setVisibility(View.GONE);
            holder.constraintLayout1.setVisibility(View.GONE);
            if(user.getDonation_request().equals("Rejected")){
                holder.imageMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(context, holder.imageMenu);
                        popup.inflate(R.menu.admin_menu3);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.raise_donation_request:
                                        raiseDonationRequest(user);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }
            else if(user.getHasDonated().equals("No")) holder.imageMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.imageMenu);
                    popup.inflate(R.menu.admin_menu5);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.mcancel_request:
                                    cancelDonationRequest(user);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
            else holder.imageMenu.setVisibility(View.GONE);
        }

        if(location.equals("UnVerifiedByAdminActivity")){
            holder.imageView1.setVisibility(View.GONE);
            holder.shareBloodRequest.setVisibility(View.GONE);
            holder.acceptBloodRequest.setVisibility(View.GONE);
            holder.exploreRequest.setText("Details");
            holder.mblood_unit.setTextColor(Color.parseColor("#3EB489"));

            holder.exploreRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AdminUserDetailActivity.class);
                    i.putExtra("user_name", user.getFirst_Name() + user.getLast_Name());
                    i.putExtra("blood_grp", user.getBlood_Group());
                    i.putExtra("type_selected", user.getTypeRequested());
                    i.putExtra("blood_unit", user.getBloodUnit());
                    i.putExtra("hospital_address", user.getHospitalName());
                    i.putExtra("haveReplacement", user.getHaveReplacement());
                    i.putExtra("contactNo", user.getPhoneNo());
                    i.putExtra("gotVerified", user.getGotVerified());
                    i.putExtra("adapter_id", user.getRequest_id());
                    i.putExtra("userId", user.getUserId());
                    i.putExtra("isVerify", user.getGotVerified());
                    i.putExtra("patient_situation", user.getPatient_situation());
                    i.putExtra("SosRequested", user.getSosRequested());
                    i.putExtra("status", user.getRequest_status());
                    i.putExtra("unit_fulfilled", user.getUnit_fulfilled());
                    i.putExtra("pdf", user.getDocument_pdf());
                    context.startActivity(i);
                }
            });
        }

        if(location.equals("VerifiedByAdminActivity")){
            holder.imageView1.setVisibility(View.GONE);
            holder.shareBloodRequest.setVisibility(View.GONE);
            holder.acceptBloodRequest.setVisibility(View.GONE);
            holder.exploreRequest.setText("Details");
            holder.mblood_unit.setTextColor(Color.parseColor("#3EB489"));

            holder.exploreRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AdminUserDetailActivity.class);
                    i.putExtra("user_name", user.getFirst_Name() + user.getLast_Name());
                    i.putExtra("blood_grp", user.getBlood_Group());
                    i.putExtra("type_selected", user.getTypeRequested());
                    i.putExtra("blood_unit", user.getBloodUnit());
                    i.putExtra("hospital_address", user.getHospitalName());
                    i.putExtra("haveReplacement", user.getHaveReplacement());
                    i.putExtra("contactNo", user.getPhoneNo());
                    i.putExtra("gotVerified", user.getGotVerified());
                    i.putExtra("adapter_id", user.getRequest_id());
                    i.putExtra("userId", user.getUserId());
                    i.putExtra("isVerify", user.getGotVerified());
                    i.putExtra("patient_situation", user.getPatient_situation());
                    i.putExtra("SosRequested", user.getSosRequested());
                    i.putExtra("status", user.getRequest_status());
                    i.putExtra("unit_fulfilled", user.getUnit_fulfilled());
                    i.putExtra("pdf", user.getDocument_pdf());
                    context.startActivity(i);
                }
            });
        }

        if(location.equals("AdminUserDetailActivity")){
            holder.mtype_selected.setVisibility(View.INVISIBLE);
            holder.mhospital_address.setText(user.getPostal_Address());
            holder.acceptBloodRequest.setVisibility(View.GONE);
            holder.exploreRequest.setVisibility(View.GONE);
            holder.shareBloodRequest.setVisibility(View.GONE);
            holder.patient_situation.setText(user.getEmail_Address());
            holder.patient_situation.setTextSize(14);
            holder.constraintLayout1.setVisibility(View.GONE);
            holder.mblood_unit.setText("Blood Unit want to Donate: " + user.getDonateUnit());
            holder.mblood_grp.setVisibility(View.GONE);
            holder.mblood_unit.setTextColor(Color.parseColor("#3EB489"));
            //holder.imageView1.setVisibility(View.GONE);
            Glide.with(context).load(user.getProfilePic()).placeholder(R.drawable.user).into(holder.imageView1);
        }

    }

    private void cancelRequest(User user) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Do you Really want to Cancel Blood Request");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference().child("Blood Request List").child(id)
                        .child(user.getRequest_id());
                HashMap<String, Object> obj1 = new HashMap<>();
                obj1.put("request_status", "Canceled");
                myRef.updateChildren(obj1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myRef.child("Donors")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            String key = dataSnapshot.getKey();
                                            myRef.child("Donors").child(key).updateChildren(obj1);
                                            database.getReference().child("User").child(key).child("Donee").child(id).child(user.getRequest_id())
                                                    .updateChildren(obj1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        //return false;
    }

    private  void  acceptBloodRequest(User user){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Do you Really want to Accept Blood Request from this Member");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                HashMap<String, Object> obj = new HashMap<>();
                obj.put("hasDonated", "Yes");
                database.getReference().child("Blood Request List").child(id).child(user.getRequest_id())
                        .child("Donors").child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User u1 = snapshot.getValue(User.class);
                                int unit_Remaining = Integer.parseInt(user.getBloodUnit()) - Integer.parseInt(user.getDonateUnit()) - Integer.parseInt(user.getUnit_fulfilled());
                                obj.put("request_status", "Completed");
                                if(u1.getHasDonated().equals("No")){
                                    database.getReference().child("Blood Request List").child(id).child(user.getRequest_id())
                                            .child("Donors").child(user.getUserId()).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getReference().child("User").child(user.getUserId()).child("Donee")
                                                            .child(id).child(user.getRequest_id()).updateChildren(obj);
                                                    HashMap<String, Object> obj1 = new HashMap<>();
                                                    int unitRemaining = Integer.parseInt(user.getBloodUnit()) - Integer.parseInt(user.getDonateUnit()) - Integer.parseInt(user.getUnit_fulfilled());
                                                    Log.e("TAG", "h  "+ user.getUnit_fulfilled() + " " + user.getBloodUnit()+ " " + user.getDonateUnit());
                                                    int unit_Fulfilled = Integer.parseInt(user.getUnit_fulfilled()) + Integer.parseInt(user.getDonateUnit());
                                                    obj1.put("unit_fulfilled", String.valueOf(unit_Fulfilled));
                                                    if(unitRemaining == 0) obj1.put("request_status", "Completed");
                                                    database.getReference().child("Blood Request List").child(id).child(user.getRequest_id()).updateChildren(obj1);
                                                }
                                            });
                                }else Toast.makeText(context, "Request Already Accepted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void activateRequest(User user) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Again Activate Blood Request?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference().child("Blood Request List").child(id)
                        .child(user.getRequest_id());
                HashMap<String, Object> obj1 = new HashMap<>();
                obj1.put("request_status", "Active");
                myRef.updateChildren(obj1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myRef.child("Donors")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            String key = dataSnapshot.getKey();
                                            myRef.child("Donors").child(key).updateChildren(obj1);
                                            database.getReference().child("User").child(key).child("Donee").child(id).child(user.getRequest_id())
                                                    .updateChildren(obj1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        //return false;
    }

    private void cancelDonationRequest(User user){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Do you Really want to Cancel Donation Request");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int id) {
                        Dialog dialog = new Dialog(context);
                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("donation_request", "Rejected");
                        database.getReference().child("User").child(auth.getCurrentUser().getUid()).child("Donee")
                                .child(user.getUserId()).child(user.getRequest_id()).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        database.getReference().child("Blood Request List").child(user.getUserId())
                                                .child(user.getRequest_id()).child("Donors").child(auth.getCurrentUser().getUid())
                                                .updateChildren(obj);
                                    }
                                });
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int id) {
                        dialog1.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void raiseDonationRequest(User user){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Again Raise Donation Request");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int id) {
                        Dialog dialog = new Dialog(context);
                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("donation_request", "accepted");
                        database.getReference().child("User").child(auth.getCurrentUser().getUid()).child("Donee")
                                .child(user.getUserId()).child(user.getRequest_id()).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        database.getReference().child("Blood Request List").child(user.getUserId())
                                                .child(user.getRequest_id()).child("Donors").child(auth.getCurrentUser().getUid())
                                                .updateChildren(obj);
                                    }
                                });
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int id) {
                        dialog1.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void sosRequest(User user){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Send Request to Admin for Emergency Case");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference().child("Blood Request List").child(id)
                        .child(user.getRequest_id());
                HashMap<String, Object> obj1 = new HashMap<>();
                obj1.put("SosRequested", "Yes");
                myRef.updateChildren(obj1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myRef.child("Donors")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            String key = dataSnapshot.getKey();
                                            myRef.child("Donors").child(key).updateChildren(obj1);
                                            database.getReference().child("User").child(key).child("Donee").child(id).child(user.getRequest_id())
                                                    .updateChildren(obj1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void Filteredlist(ArrayList<User> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mtype_selected, mblood_grp, muser_name, mblood_unit, mhospital_address, patient_situation, exploreRequest, acceptBloodRequest, shareBloodRequest;
        ConstraintLayout constraintLayout1;
        ImageView imageView1, imageMenu;;
        MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mtype_selected = itemView.findViewById(R.id.type_selected);
            mblood_grp = itemView.findViewById(R.id.blood_grp);
            muser_name = itemView.findViewById(R.id.user_name);
            mblood_unit = itemView.findViewById(R.id.blood_unit);
            mhospital_address = itemView.findViewById(R.id.hosital_name);
            patient_situation = itemView.findViewById(R.id.patient_situation);
            exploreRequest = itemView.findViewById(R.id.explore_blood_request);
            acceptBloodRequest = itemView.findViewById(R.id.accept_blood_request);
            shareBloodRequest = itemView.findViewById(R.id.share_blood_request);
            constraintLayout1 = itemView.findViewById(R.id.constraint_layout1);
            imageView1 = itemView.findViewById(R.id.imageView1);
            cardView = itemView.findViewById(R.id.card_view1);
            imageMenu = itemView.findViewById(R.id.menu1);

        }
    }

    private void sendNotification(PushNotification notification) {

        ApiUtilities.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
