package com.printdinc.printd.service;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationAPIKeyInterceptor implements Interceptor {

    private String authToken;

    public AuthenticationAPIKeyInterceptor(String token) {
        this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("X-Api-Key", authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
