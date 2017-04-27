package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.ConfigFile;
import com.printdinc.printd.model.User;
import com.printdinc.printd.service.HerokuService;

import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * View model for the ConfigPrinterActivity
 */
public class ConfigPrinterViewModel implements ViewModel {

    private static final String TAG = "ConfigPrinterViewModel";

    public ObservableInt infoMessageVisibility;
    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    //    public ObservableInt searchButtonVisibility;
    public ObservableField<String> infoMessage;

    private Context context;
    private Subscription subscription;
    private List<ConfigFile> files;
    private DataListener dataListener;
    public ObservableField<String> configFileName;
    private String currentConfig;
//    private String editTextUsernameValue;

    public ConfigPrinterViewModel(Context context, DataListener dataListener) {
        this.context = context;
        this.dataListener = dataListener;
        infoMessageVisibility = new ObservableInt(View.VISIBLE);
        progressVisibility = new ObservableInt(View.INVISIBLE);
        recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
//        searchButtonVisibility = new ObservableInt(View.GONE);
        infoMessage = new ObservableField<>(context.getString(R.string.default_info_message));

        configFileName = new ObservableField<String>("Loading...");
        currentConfig = null;

        loadConfigFiles();
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

    private void retrieveCurrentConfig() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        HerokuService herokuService = application.getHerokuService();
        subscription = herokuService.getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error loading Thingiverse collections ", error);

                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(TAG, "User loaded " + user);
                        ConfigPrinterViewModel.this.currentConfig = user.getPrinterconfigid();

                        ConfigFile file = null;
                        for (int i = 0; i < files.size(); i++) {
                            Log.i(TAG, "Comparing IDs: " + files.get(i).getId() + user.getPrinterconfigid());
                            if (files.get(i).getId().equals(user.getPrinterconfigid()))
                                ConfigPrinterViewModel.this.configFileName.set(files.get(i).getFilename());
                        }


                    }
                });
    }

    private void loadConfigFiles() {
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.INVISIBLE);
        infoMessageVisibility.set(View.INVISIBLE);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        HerokuService herokuService = application.getHerokuService();
        subscription = herokuService.configFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<List<ConfigFile>>() {
                    @Override
                    public void onCompleted() {
                        if (dataListener != null)
                            dataListener.onConfigFilesChanged(files);
                        progressVisibility.set(View.INVISIBLE);
                        if (!files.isEmpty()) {
                            recyclerViewVisibility.set(View.VISIBLE);
                            retrieveCurrentConfig();
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
                    public void onNext(List<ConfigFile> files) {
                        Log.i(TAG, "Collections loaded " + files);
                        ConfigPrinterViewModel.this.files = files;
                    }
                });
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

    public interface DataListener {
        void onConfigFilesChanged(List<ConfigFile> files);
    }
}
