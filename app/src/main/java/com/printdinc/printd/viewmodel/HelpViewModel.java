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
    public int answer1Visibility;
    public int answer2Visibility;
    public int answer3Visibility;
    public int answer4Visibility;




    public HelpViewModel(Context context) {

        this.context = context;
        answer1Visibility = View.GONE;
        answer2Visibility = View.GONE;
        answer3Visibility = View.GONE;
        answer4Visibility = View.GONE;

    }

    @Override
    public void destroy() {
        context = null;
    }



    public void onClickQuestion1(View view) {
        if (answer1Visibility == View.GONE) {
            answer1Visibility = View.VISIBLE;
        }
    }

    public void onClickQuestion2(View view) {
        if (answer2Visibility == View.GONE) {
            answer2Visibility = View.VISIBLE;
        }
    }

    public void onClickQuestion3(View view) {
        if (answer3Visibility == View.GONE) {
            answer3Visibility = View.VISIBLE;
        }
    }

    public void onClickQuestion4(View view) {
        if (answer4Visibility == View.GONE) {
            answer4Visibility = View.VISIBLE;
        }
    }


}
