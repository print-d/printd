package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.printdinc.printd.model.ThingiverseCollection;
import com.printdinc.printd.view.ThingiverseCollectionActivity;

/**
 * View model for each item in the repositories RecyclerView
 */
public class ItemCollectionViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = "ItemCollectionViewModel";

    private ThingiverseCollection collection;
    private Context context;

    public ItemCollectionViewModel(Context context, ThingiverseCollection collection) {
        this.collection = collection;
        this.context = context;
    }

    public String getName() {
        return collection.name;
    }

    public void onItemClick(View view) {
        context.startActivity(ThingiverseCollectionActivity.newIntent(context, collection));
    }

    public String getThumbnailUrl() {
        return collection.thumbnail;
    }
    public String getThumbnailUrl_1() {
        return collection.thumbnail_1;
    }
    public String getThumbnailUrl_2() {
        return collection.thumbnail_2;
    }
    public String getThumbnailUrl_3() {
        return collection.thumbnail_3;
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setCollection(ThingiverseCollection collection) {
        this.collection = collection;
        notifyChange();
    }

    @Override
    public void destroy() {
        //In this case destroy doesn't need to do anything because there is not async calls
    }

}
