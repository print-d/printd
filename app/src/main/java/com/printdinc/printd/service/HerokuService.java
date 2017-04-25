package com.printdinc.printd.service;

import com.printdinc.printd.model.Login;
import com.printdinc.printd.model.Printer;
import com.printdinc.printd.model.PrinterData;
import com.printdinc.printd.model.User;

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

    @GET("dimensions/")
    Observable<Printer> printerDimensions(@Body Printer printer_param);

    @GET("userdata/")
    Observable<User> getUserData();

    //@GET("configfile/")
    //Observable<>


}