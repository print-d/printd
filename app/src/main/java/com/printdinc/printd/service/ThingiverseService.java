package com.printdinc.printd.service;

import com.printdinc.printd.model.ThingiverseCollection;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.model.ThingiverseThingFile;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface ThingiverseService {

    @GET("users/{username}/collections")
    Observable<List<ThingiverseCollection>> collections(@Path("username") String username);

    @GET("/collections/{id}/things")
    Observable<List<ThingiverseCollectionThing>> collectionThings(@Path("id") String collection_id);

    @GET("/things/{id}/files")
    Observable<List<ThingiverseThingFile>> thingFiles(@Path("id") String thing_id);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}