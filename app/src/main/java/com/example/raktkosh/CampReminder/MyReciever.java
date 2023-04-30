package com.example.raktkosh.CampReminder;

import static com.example.raktkosh.Notification.Constant.TOPIC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;

import com.example.raktkosh.Notification.ApiUtilities;
import com.example.raktkosh.Notification.NotificationData;
import com.example.raktkosh.Notification.PushNotification;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReciever extends BroadcastReceiver {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String id = auth.getCurrentUser().getUid();

    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "n7");
        mp = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mp.setLooping(true);
        mp.start();
        Log.e("TAG", "n8");
        PushNotification notification = new PushNotification(new NotificationData("hello", "whatsapp"), "/topics/"+ id);
        sendNotification(notification);
        Log.e("TAG", "n9");
    }

    private void sendNotification(PushNotification notification) {

        ApiUtilities.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful()){
                    //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                //Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
