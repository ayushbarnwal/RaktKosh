package com.example.raktkosh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.raktkosh.Adapter.MessageAdapter;
import com.example.raktkosh.Fragment.RaisedRequestFragment;
import com.example.raktkosh.Models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UserChatActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    String ReceiverId, SenderId, DonorName, DonorPhoto;
    String senderRoom,receiverRoom;
    ImageView donorImage, send_msg, select_file;
    TextView donor_name;
    EditText msg_type;
    RecyclerView msg_recycler_view;
    ScrollView chatScroll;
    MessageAdapter messageAdapter;
    FirebaseAuth auth;
    DatabaseReference myRef;
    StorageReference storageReference;
    final ArrayList<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        auth = FirebaseAuth.getInstance();
        SenderId = auth.getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference();

        InitializeWidget();

        fetchData();

        messageAdapter = new MessageAdapter(messageList, UserChatActivity.this, ReceiverId);
        msg_recycler_view.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msg_recycler_view.setLayoutManager(layoutManager);

        chatScroll.fullScroll(View.FOCUS_DOWN);

        senderRoom = SenderId + ReceiverId;
        receiverRoom = ReceiverId + SenderId;

        SendMessage();

        FetchMessage();

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popUp = new PopupMenu(UserChatActivity.this,select_file);
                popUp.setOnMenuItemClickListener(UserChatActivity.this);
                popUp.inflate(R.menu.send_document_selection_menu);
                popUp.show();
            }
        });


    }

    private void FetchMessage() {

        myRef.child("Chats")
                .child(senderRoom).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageList.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message model = snapshot1.getValue(Message.class);
                            model.setMessageId(snapshot1.getKey());
                            messageList.add(model);
                        }
                        messageAdapter.notifyDataSetChanged();   //in order to see msg at run time... if we not do this then msg seen only when we press back button
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void SendMessage() {

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgTyped = msg_type.getText().toString();
                if(!TextUtils.isEmpty(msgTyped)){
                    final Message model = new Message(SenderId,msgTyped);
                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                    model.setTimestamp(new Date().getTime());
                    msg_type.setText(" ");

                    myRef.child("Chats")                //to store msg
                            .child(senderRoom)
                            .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {          //push will allote node to different msgs
                                @Override
                                public void onSuccess(Void unused) {
                                    myRef.child("Chats").child(receiverRoom).push()
                                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {}
                                            });
                                }
                            });
                }
                chatScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    private void fetchData() {
        ReceiverId = getIntent().getStringExtra("DonorId");
        DonorName = getIntent().getStringExtra("DonorName");
        DonorPhoto = getIntent().getStringExtra("DonorProfilePic");
        select_file=findViewById(R.id.select_file);

        donor_name.setText(DonorName);
        Glide.with(this).load(DonorPhoto).placeholder(R.drawable.user).into(donorImage);
    }

    private void InitializeWidget() {
        donorImage = findViewById(R.id.donor_profile_image);
        donor_name = findViewById(R.id.donor_name);
        msg_type = findViewById(R.id.msg_type);
        send_msg=findViewById(R.id.send_msg);
        msg_recycler_view = findViewById(R.id.chat_recycler_view5);
        chatScroll =findViewById(R.id.chat_scroll);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.select_photo:
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,33);
//            case R.id.select_files:
//                Toast.makeText(this, "file", Toast.LENGTH_SHORT).show();
//                Intent i1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                //i1.addCategory(Intent.CATEGORY_OPENABLE);
//                i1.setType("application/pdf");
//                i1.addCategory(Intent.CATEGORY_DEFAULT);
//                startActivityForResult(i1,35);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 33 && resultCode == RESULT_OK){
            if(data.getData()!=null){
                Uri sFile = data.getData();

                storageReference = FirebaseStorage.getInstance().getReference().child("ChatFiles").child(sFile.getLastPathSegment());        //to upload the image in storage here we given uid so when user update his profile pic then it will get overridden in storage.... so if we want to create different id for different image uploaded then use push method
                storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(UserChatActivity.this,"file Uploaded",Toast.LENGTH_SHORT).show();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Calendar time = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                                Message model = new Message(SenderId,uri.toString(),new Date().getTime());
                                myRef.getDatabase().getReference().child("Chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        myRef.getDatabase().getReference().child("Chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(UserChatActivity.this,"file Uploaded in databse",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

            }
        }

//        if(requestCode==35 && resultCode==RESULT_OK){
//            if(data.getData()!=null){
//                Uri sFile = data.getData();
//
//                storageReference = FirebaseStorage.getInstance().getReference().child("ChatFiles").child(sFile.getLastPathSegment());        //to upload the image in storage here we given uid so when user update his profile pic then it will get overridden in storage.... so if we want to create different id for different image uploaded then use push method
//                storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(UserChatActivity.this,"file Uploaded",Toast.LENGTH_SHORT).show();
//                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Calendar time = Calendar.getInstance();
//                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
//                                Message model = new Message(SenderId,uri.toString(),new Date().getTime());
//                                HashMap<String , Object> obj = new HashMap<>();     // to update on firebase
//                                obj.put("pdfUrl",uri.toString());
//                                obj.put("uid",model.getuId());
//                                obj.put("timestamp",model.getTimestamp());
//                                myRef.getDatabase().getReference().child("Chats").child(receiverRoom).push().setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        myRef.getDatabase().getReference().child("Chats").child(senderRoom).push().setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//                                                Toast.makeText(UserChatActivity.this,"file Uploaded in databse",Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                });
//                            }
//                        });
//                    }
//                });
//
//            }else {
//                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
//            }
//        }

    }
}