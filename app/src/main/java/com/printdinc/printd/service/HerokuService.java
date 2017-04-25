package com.printdinc.printd.service;

import com.printdinc.printd.model.Login;
import com.printdinc.printd.model.Printer;
import com.printdinc.printd.model.PrinterData;
import com.printdinc.printd.model.ThingiverseCollection;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.model.ThingiverseThingFile;
import com.printdinc.printd.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface HerokuService {

    @POST("login/")
    Observable<String> login(@Body Login login_param);

    @POST("create/")
    Observable<String> createUser(@Body User user);

    @GET("printerdata/")
    Observable<PrinterData> printerData(@Body PrinterData printer_data);

    @GET("dimensions/")
    Observable<Printer> printerDimensions(@Body Printer printer_param);

    //@GET("configfile/")
    //Observable<>


}