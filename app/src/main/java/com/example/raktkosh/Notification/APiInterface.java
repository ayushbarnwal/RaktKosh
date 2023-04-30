package com.example.raktkosh.Notification;

import static com.example.raktkosh.Notification.Constant.CONTENT_TYPE;
import static com.example.raktkosh.Notification.Constant.SERVER_KEY;

import android.util.Log;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APiInterface {

    @Headers({"Authorization: key="+SERVER_KEY, "Content-Type:"+CONTENT_TYPE})
    @POST("fcm/send")
    Call<PushNotification> sendNotification(@Body PushNotification notification);

}
