package com.printdinc.printd.service;

import com.printdinc.printd.model.ThingiverseCollection;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.model.ThingiverseThingFile;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ThingiverseService {

    @GET("users/{username}/collections")
    Observable<List<ThingiverseCollection>> collections(@Path("username") String username);

    @GET("/collections/{id}/things")
    Observable<List<ThingiverseCollectionThing>> collectionThings(@Path("id") String collection_id);

    @GET("/things/{id}/files")
    Observable<List<ThingiverseThingFile>> thingFiles(@Path("id") String thing_id);

    @GET("/files/{id}/download")
    Observable<Object> downloadFile(@Path("id") String file_id);
}