package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

//import com.squareup.picasso.Picasso;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.service.ThingiverseService;
import com.printdinc.printd.model.ThingiverseCollection;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * ViewModel for the ThingiverseCollectionActivity
 */
public class ThingiverseCollectionViewModel implements ViewModel {

    private static final String TAG = "TVCollectionViewModel";

    public ObservableInt infoMessageVisibility;
    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    public ObservableField<String> infoMessage;

    private Context context;
    private Subscription subscription;
    private List<ThingiverseCollectionThing> collectionThings;
    private ThingiverseCollectionViewModel.DataListener dataListener;
    private ThingiverseCollection collection;

    public ThingiverseCollectionViewModel(Context context, ThingiverseCollectionViewModel.DataListener dataListener, ThingiverseCollection collection) {
        this.context = context;
        this.dataListener = dataListener;
        infoMessageVisibility = new ObservableInt(View.VISIBLE);
        progressVisibility = new ObservableInt(View.INVISIBLE);
        recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
        infoMessage = new ObservableField<>(context.getString(R.string.default_info_message));
        this.collection = collection;

        loadThingiverseCollection(Long.toString(collection.id));
    }

    public void setDataListener(ThingiverseCollectionViewModel.DataListener dataListener) {
        this.dataListener = dataListener;
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
        dataListener = null;
    }

    private void loadThingiverseCollection(String collectionID) {
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.INVISIBLE);
        infoMessageVisibility.set(View.INVISIBLE);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        ThingiverseService thingiverseService = application.getThingiverseService();
        subscription = thingiverseService.collectionThings(collectionID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<List<ThingiverseCollectionThing>>() {
                    @Override
                    public void onCompleted() {
                        if (dataListener != null)
                            dataListener.onCollectionsChanged(collectionThings);
                        progressVisibility.set(View.INVISIBLE);
                        if (!collectionThings.isEmpty()) {
                            recyclerViewVisibility.set(View.VISIBLE);
                        } else {
                            infoMessage.set(context.getString(R.string.text_empty_repos));
                            infoMessageVisibility.set(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error loading Thingiverse collection ", error);
                        progressVisibility.set(View.INVISIBLE);
                        if (isHttp404(error)) {
                            infoMessage.set(context.getString(R.string.error_username_not_found));
                        } else {
                            infoMessage.set(context.getString(R.string.error_loading_collections));
                        }
                        infoMessageVisibility.set(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<ThingiverseCollectionThing> collectionThings) {
                        Log.i(TAG, "Collection loaded " + collectionThings);
                        ThingiverseCollectionViewModel.this.collectionThings = collectionThings;
                    }
                });
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

    public interface DataListener {
        void onCollectionsChanged(List<ThingiverseCollectionThing> collectionThings);
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                //.placeholder(R.mipmap.placeholder)
                .into(view);
    }
}
