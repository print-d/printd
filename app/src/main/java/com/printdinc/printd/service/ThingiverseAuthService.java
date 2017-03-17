package com.printdinc.printd.service;


import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by andrewthomas on 3/9/17.
 */

public interface ThingiverseAuthService {

    @FormUrlEncoded
    @POST("/login/oauth/access_token")
    Observable<String> getAccessToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("code") String code);
}
