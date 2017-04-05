package com.printdinc.printd.service;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OctoprintServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl("http://192.168.0.108/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(new Gson()));

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(
            Class<S> serviceClass, final String authToken) {

        Retrofit retrofit = null;

        if (!TextUtils.isEmpty(authToken)) {

            AuthenticationAPIKeyInterceptor interceptor =
                    new AuthenticationAPIKeyInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());

            }
        }

        retrofit = builder
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        return retrofit.create(serviceClass);
    }
}