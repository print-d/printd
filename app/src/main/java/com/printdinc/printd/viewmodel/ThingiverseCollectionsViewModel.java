package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.regex.Pattern;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.service.ThingiverseService;
import com.printdinc.printd.model.ThingiverseCollection;

/**
 * View model for the ThingiverseCollectionsActivity
 */
public class ThingiverseCollectionsViewModel implements ViewModel {

    private static final String TAG = "TVCollectionsViewModel";

    public ObservableInt infoMessageVisibility;
    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    //    public ObservableInt searchButtonVisibility;
    public ObservableField<String> infoMessage;

    private Context context;
    private Subscription subscription;
    private List<ThingiverseCollection> collections;
    private DataListener dataListener;
//    private String editTextUsernameValue;

    Pattern token_parse = Pattern.compile("access_token=(.+?)&token_type=(\\w+)");

    public ThingiverseCollectionsViewModel(Context context, DataListener dataListener) {
        this.context = context;
        this.dataListener = dataListener;
        infoMessageVisibility = new ObservableInt(View.VISIBLE);
        progressVisibility = new ObservableInt(View.INVISIBLE);
        recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
//        searchButtonVisibility = new ObservableInt(View.GONE);
        infoMessage = new ObservableField<>(context.getString(R.string.default_info_message));

        loadThingiverseCollections(context.getString(R.string.me_user));
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
        dataListener = null;
    }

//    public boolean onSearchAction(TextView view, int actionId, KeyEvent event) {
//        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            String username = view.getText().toString();
//            if (username.length() > 0) loadThingiverseCollections(username);
//            return true;
//        }
//        return false;
//    }

//    public void onClickSearch(View view) {
//        loadThingiverseCollections(editTextUsernameValue);
//    }

//    public TextWatcher getUsernameEditTextWatcher() {
//        return new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                editTextUsernameValue = charSequence.toString();
//                searchButtonVisibility.set(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        };
//    }

    private void loadThingiverseCollections(String username) {
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.INVISIBLE);
        infoMessageVisibility.set(View.INVISIBLE);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        ThingiverseService thingiverseService = application.getThingiverseService();
        subscription = thingiverseService.collections(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<List<ThingiverseCollection>>() {
                    @Override
                    public void onCompleted() {
                        if (dataListener != null)
                            dataListener.onCollectionsChanged(collections);
                        progressVisibility.set(View.INVISIBLE);
                        if (!collections.isEmpty()) {
                            recyclerViewVisibility.set(View.VISIBLE);
                        } else {
                            infoMessage.set(context.getString(R.string.text_empty_repos));
                            infoMessageVisibility.set(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error loading Thingiverse collections ", error);
                        progressVisibility.set(View.INVISIBLE);
                        if (isHttp404(error)) {
                            infoMessage.set(context.getString(R.string.error_username_not_found));
                        } else {
                            infoMessage.set(context.getString(R.string.error_loading_collections));
                        }
                        infoMessageVisibility.set(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<ThingiverseCollection> collections) {
                        Log.i(TAG, "Collections loaded " + collections);
                        ThingiverseCollectionsViewModel.this.collections = collections;
                    }
                });
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

    public interface DataListener {
        void onCollectionsChanged(List<ThingiverseCollection> collections);
    }
}
