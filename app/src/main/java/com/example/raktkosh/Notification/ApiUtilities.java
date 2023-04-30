package com.example.raktkosh.Notification;

import static com.example.raktkosh.Notification.Constant.BASE_URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {

    private static Retrofit retrofit = null;

    public static APiInterface getClient(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APiInterface.class);
    }

}
