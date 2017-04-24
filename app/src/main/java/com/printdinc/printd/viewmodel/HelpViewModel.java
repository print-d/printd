package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityBedLevelBinding;
import com.printdinc.printd.model.ConnectionState;
import com.printdinc.printd.model.ConnectionStateState;
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

public class HelpViewModel implements ViewModel {

    private static final String TAG = "MainViewModel";

    private Context context;

    public HelpViewModel(Context context) {
        this.context = context;
    }

    @Override
    public void destroy() {
        context = null;
    }






}
