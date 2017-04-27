package com.printdinc.printd.service;

import com.google.gson.JsonObject;
import com.printdinc.printd.model.ConfigFile;
import com.printdinc.printd.model.Login;
import com.printdinc.printd.model.Printer;
import com.printdinc.printd.model.PrinterData;
import com.printdinc.printd.model.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface HerokuService {

    @POST("login/")
    Observable<String> login(@Body Login login_param);

    @POST("create/")
    Observable<String> createUser(@Body User user);

    @GET("printerdata/")
    Observable<PrinterData> printerData();

    @POST("userdata/")
    Observable<String> editUser(@Body User user);

    @GET("userdata/")
    Observable<User> getUser();

    @GET("dimensions/")
    Observable<Printer> printerDimensions();

    @GET("userdata/")
    Observable<User> getUserData();

    @GET("configfilelist/")
    Observable<List<ConfigFile>> configFiles();

    @GET("configfile/")
    Observable<JsonObject> configFile();

}