package com.example.shopee.retrofit;

import com.example.shopee.model.NotiResponse;
import com.example.shopee.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotification {
    @Headers(
            {
                    "content-type: application/json",
                    "authorization: key=AAAAoadtBtk:APA91bFo884EpsFH4HWXskompVyzQ6NgC4bZqBILmlDn5j8rtUeHYxpKFOuDWA-KrYyLdwgwZifwzV5afUfPRQifdTJJmxtJRlYBsUbfJaqRP3C_h8_CQ2gZrvmsNJIW953bPl6nDsFZ"
            }
    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNotification(@Body NotiSendData data);
}
