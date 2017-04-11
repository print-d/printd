package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.database.Observable;
import android.databinding.ObservableInt;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.PrintHeadCommand;
import com.printdinc.printd.service.OctoprintService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by andrewthomas on 3/9/17.
 */

public class BedLevelViewModel implements ViewModel {

    private static final String TAG = "MainViewModel";

    private Context context;
    private Subscription subscription;
    private int current_step;

    public Observable<String> buttonText;

    public ObservableInt progressVisibility;

    Pattern token_parse = Pattern.compile("access_token=(.+?)&token_type=(\\w+)");


    public BedLevelViewModel(Context context) {
        this.context = context;
        progressVisibility = new ObservableInt(View.INVISIBLE);
        firstStep();

    }

    public void resumeActivity() {
        progressVisibility.set(View.INVISIBLE);
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
    }

    private void firstStep() {
        current_step = 1;
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
        PrintHeadCommand phc = new PrintHeadCommand("home", new ArrayList<String>(Arrays.asList("x", "y", "z")), 0, 0, 0);
        subscription = octoprintService.printHeadHomeCommand(phc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error printing", error);

                    }

                    @Override
                    public void onNext(ResponseBody r) {
                        Log.i(TAG, "ResponseBody loaded " + r);
                    }
                });

    }


    public void onClickNext(View view) {

        if (current_step > 4){
            // Finish
        }
    }

    private void secondStep() {


    }

    private void thirdStep() {

    }

    private void fourthStep() {

    }

}
