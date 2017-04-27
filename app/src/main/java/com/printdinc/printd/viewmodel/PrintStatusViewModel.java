package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.transition.Visibility;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityBedLevelBinding;
import com.printdinc.printd.model.ConnectionState;
import com.printdinc.printd.model.ConnectionStateState;
import com.printdinc.printd.model.JobStatus;
import com.printdinc.printd.model.JobStatusState;
import com.printdinc.printd.model.PrintHeadCommand;
import com.printdinc.printd.model.SimpleCommand;
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

public class PrintStatusViewModel implements ViewModel {

    private static final String TAG = "MainViewModel";

    private Context context;
    private Activity activity;

    private Subscription subscription;

    public ObservableField<String> completion;
    public ObservableField<String> printTime;
    public ObservableField<String> printTimeLeft;


    public PrintStatusViewModel(Context context, Activity activity) {

        this.context = context;
        this.activity = activity;
        completion = new ObservableField<String>("");
        printTime = new ObservableField<String>("");
        printTimeLeft = new ObservableField<String>("");
        getJobStatus();
    }

    @Override
    public void destroy() {
        context = null;
    }

    public void getJobStatus() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
        subscription = octoprintService.getJobInformation()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<JobStatus>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error printing", error);
                        new AlertDialog.Builder(context)
                                .setIcon(0)
                                .setTitle("Error Checking Print Status")
                                .setMessage("There was an error connecting to your printer. Make sure your printer is turned on.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Go back here
                                        activity.finish();

                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onNext(JobStatus js) {
                        // Winning again?
                        JobStatusState jss = js.getProgress();

                        // This comes in as a double. Casting it to int to make it round.
                        completion.set(String.valueOf((int)jss.getCompletion()));
                        printTime.set(String.valueOf(jss.getPrintTime() / 60));
                        printTimeLeft.set(String.valueOf(jss.getPrintTimeLeft() / 60));
                    }
                });
    }



}
