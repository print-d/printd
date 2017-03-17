package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.view.ThingiverseThingActivity;

/**
 * Created by andrewthomas on 3/16/17.
 */

public class ItemThingViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = "ItemCollectionViewModel";

    private ThingiverseCollectionThing collectionThing;
    private Context context;

    public ItemThingViewModel(Context context, ThingiverseCollectionThing collectionThing) {
        this.collectionThing = collectionThing;
        this.context = context;
    }

    public String getName() {
        return collectionThing.name;
    }

    public void onItemClick(View view) {
        context.startActivity(ThingiverseThingActivity.newIntent(context, collectionThing));
    }

    public String getThumbnailUrl() {
        return collectionThing.thumbnail;
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setCollection(ThingiverseCollectionThing collectionThing) {
        this.collectionThing = collectionThing;
        notifyChange();
    }

    @Override
    public void destroy() {
        //In this case destroy doesn't need to do anything because there is not async calls
    }
}
