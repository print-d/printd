package com.printdinc.printd.service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface OctoprintService {

    @Multipart
    @POST("api/files/{filename}")
    Call<ResponseBody> uploadFile(@Path("filename") String filename, @Part MultipartBody.Part file);
}