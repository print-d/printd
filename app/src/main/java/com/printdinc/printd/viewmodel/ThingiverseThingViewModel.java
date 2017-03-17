package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;
import android.widget.ImageView;

import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.squareup.picasso.Picasso;

import rx.Subscription;

/**
 * Created by andrewthomas on 3/16/17.
 */

public class ThingiverseThingViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = "TVThingViewModel";

    private ThingiverseCollectionThing thing;
    private Context context;
    private Subscription subscription;

    public ObservableField<String> ownerName;
    public ObservableField<String> ownerEmail;
    public ObservableField<String> ownerLocation;
    public ObservableInt ownerEmailVisibility;
    public ObservableInt ownerLocationVisibility;
    public ObservableInt ownerLayoutVisibility;

    public ThingiverseThingViewModel(Context context, final ThingiverseCollectionThing thing) {
        this.thing = thing;
        this.context = context;
        this.ownerName = new ObservableField<>();
        this.ownerEmail = new ObservableField<>();
        this.ownerLocation = new ObservableField<>();
        this.ownerLayoutVisibility = new ObservableInt(View.INVISIBLE);
        this.ownerEmailVisibility = new ObservableInt(View.VISIBLE);
        this.ownerLocationVisibility = new ObservableInt(View.VISIBLE);
        // Trigger loading the rest of the user data as soon as the view model is created.
        // It's odd having to trigger this from here. Cases where accessing to the data model
        // needs to happen because of a change in the Activity/Fragment lifecycle
        // (i.e. an activity created) don't work very well with this MVVM pattern.
        // It also makes this class more difficult to test. Hopefully a better solution will be found
        //loadFullUser(collection.owner.url);
    }

    @Override
    public void destroy() {
        this.context = null;
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
    }

    public String getThumbnailUrl() {
        return thing.thumbnail;
    }
    public String getName() { return thing.name; }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                //.placeholder(R.mipmap.placeholder)
                .into(view);
    }
}
