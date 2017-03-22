package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.model.ThingiverseThingFile;
import com.printdinc.printd.service.ThingiverseService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by andrewthomas on 3/16/17.
 */

public class ThingiverseThingViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = "TVThingViewModel";

    public ObservableInt infoMessageVisibility;
    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    public ObservableInt printButtonVisibility;
    public ObservableField<String> infoMessage;

    private Context context;
    private Subscription subscription;
    private List<ThingiverseThingFile> thingFiles;
    private ThingiverseThingViewModel.DataListener dataListener;
    private ThingiverseCollectionThing thing;

    public ThingiverseThingViewModel(Context context, ThingiverseThingViewModel.DataListener dataListener, ThingiverseCollectionThing thing) {
        this.context = context;
        this.dataListener = dataListener;
        infoMessageVisibility = new ObservableInt(View.VISIBLE);
        progressVisibility = new ObservableInt(View.INVISIBLE);
        recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
        printButtonVisibility = new ObservableInt(View.INVISIBLE);
        infoMessage = new ObservableField<>(context.getString(R.string.default_info_message));
        this.thing = thing;

        loadThingiverseFiles(Long.toString(thing.id));
    }

    public void setDataListener(ThingiverseThingViewModel.DataListener dataListener) {
        this.dataListener = dataListener;
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
        dataListener = null;
    }

    private void loadThingiverseFiles(String thingID) {
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.INVISIBLE);
        infoMessageVisibility.set(View.INVISIBLE);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        ThingiverseService thingiverseService = application.getThingiverseService();
        subscription = thingiverseService.thingFiles(thingID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<List<ThingiverseThingFile>>() {
                    @Override
                    public void onCompleted() {
                        if (dataListener != null)
                            dataListener.onCollectionsChanged(thingFiles);
                        progressVisibility.set(View.INVISIBLE);
                        if (!thingFiles.isEmpty()) {
                            recyclerViewVisibility.set(View.VISIBLE);
                        } else {
                            infoMessage.set(context.getString(R.string.text_empty_repos));
                            infoMessageVisibility.set(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error loading Thingiverse files ", error);
                        progressVisibility.set(View.INVISIBLE);
                        if (isHttp404(error)) {
                            infoMessage.set(context.getString(R.string.error_username_not_found));
                        } else {
                            infoMessage.set(context.getString(R.string.error_loading_collections));
                        }
                        infoMessageVisibility.set(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<ThingiverseThingFile> thingFiles) {
                        String adjname;
                        Log.i(TAG, "Collection loaded " + thingFiles);

                        ArrayList<ThingiverseThingFile> filteredFiles = new ArrayList<ThingiverseThingFile>();

                        for (ThingiverseThingFile file: thingFiles) {
                            adjname = file.name.toLowerCase().replaceAll("\\s", "");
                            if (adjname.endsWith(".stl")) {
                                filteredFiles.add(file);
                            }
                        }
                        ThingiverseThingViewModel.this.thingFiles = filteredFiles;
                    }
                });
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

    public interface DataListener {
        void onCollectionsChanged(List<ThingiverseThingFile> thingFiles);
    }


    public String getThumbnailUrl() {
        return thing.thumbnail;
    }
    public String getName() { return thing.name; }

    public void onClickPrint(View view) {

    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                //.placeholder(R.mipmap.placeholder)
                .into(view);
    }
}