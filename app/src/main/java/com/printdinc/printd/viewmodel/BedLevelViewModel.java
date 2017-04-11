package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.model.PrintHeadCommand;
import com.printdinc.printd.service.OctoprintService;

import java.util.ArrayList;
import java.util.Arrays;

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

    public ObservableField<String> buttonText;

    public ObservableInt progressVisibility;


    public BedLevelViewModel(Context context) {
        this.context = context;
        progressVisibility = new ObservableInt(View.INVISIBLE);
        firstStep();
        buttonText = new ObservableField<String>("Leggo");

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

    private void doPrintHeadCommand(PrintHeadCommand phc) {
        current_step = 1;
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
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
                        Log.e(TAG, "Error: ", error);
                    }

                    @Override
                    public void onNext(ResponseBody r) {
                        Log.i(TAG, "ResponseBody loaded " + r);
                    }
                });
    }

    private void firstStep() {
        PrintHeadCommand phc = new PrintHeadCommand("home", new ArrayList<String>(Arrays.asList("x", "y", "z")), 0, 0, 0);
        doPrintHeadCommand(phc);
    }

    private PrintHeadCommand jogCommand(int x, int y, int z) {
        PrintHeadCommand phc = new PrintHeadCommand("jog", new ArrayList<String>(Arrays.asList("x", "y", "z")), x, y, z);
        return phc;
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
