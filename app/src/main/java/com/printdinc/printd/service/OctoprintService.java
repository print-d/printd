package com.printdinc.printd.service;

import com.printdinc.printd.model.ConnectionState;
import com.printdinc.printd.model.SimpleCommand;
import com.printdinc.printd.model.SliceCommand;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

public interface OctoprintService {

    @GET("api/connection")
    Observable<ConnectionState> getConnectionState();

    @POST("api/connection")
    Observable<ResponseBody> connectCommand(@Body SimpleCommand command);

    @Multipart
    @POST("api/files/{location}")
    Call<ResponseBody> uploadFile(@Path("location") String location,
                                  @Part MultipartBody.Part file);

    @POST("api/files/{location}/{filename}")
    Observable<ResponseBody> issuePrintCommand(@Path("location") String location,
                                               @Path("filename") String filename,
                                               @Body SliceCommand sc);
}