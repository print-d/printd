package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.service.OctoprintServiceGenerator;
import com.printdinc.printd.service.ThingiverseAuthService;
import com.printdinc.printd.service.ThingiverseAuthServiceGenerator;
import com.printdinc.printd.service.ThingiverseService;
import com.printdinc.printd.service.ThingiverseServiceGenerator;
import com.printdinc.printd.view.BedLevelActivity;
import com.printdinc.printd.view.CreateAccountActivity;
import com.printdinc.printd.view.ThingiverseCollectionsActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by andrewthomas on 3/9/17.
 */

public class MainViewModel implements ViewModel {

    private static final String TAG = "MainViewModel";

    private Context context;
    private Subscription subscription;

    public ObservableInt progressVisibility;

    Pattern token_parse = Pattern.compile("access_token=(.+?)&token_type=(\\w+)");


    public MainViewModel(Context context) {
        this.context = context;
        progressVisibility = new ObservableInt(View.INVISIBLE);
    }

    public void thingiverseLogin(Uri uri) {

        progressVisibility.set(View.VISIBLE);

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        if (uri != null && uri.toString().startsWith(context.getString(R.string.redirect_uri))) {
            // use the parameter your API exposes for the code (mostly it's "code")
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // get access token
                if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();

                PrintdApplication application = PrintdApplication.get(context);
                ThingiverseAuthService thingiverseAuthService = application.getThingiverseAuthService();

                //TODO reshape this call to be to Heroku server instead of thingiverse
                subscription = thingiverseAuthService.getAccessToken(context.getString(R.string.client_id), context.getString(R.string.client_secret), code)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(application.defaultSubscribeScheduler())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                progressVisibility.set(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Throwable error) {
                                Log.e(TAG, "Error receiving token ", error);
                                progressVisibility.set(View.INVISIBLE);
                            }

                            @Override
                            public void onNext(String token) {
                                Log.i(TAG, "Token received " + token);
                                if (token != null)
                                {
                                    // extract the token from the response
                                    Matcher m = token_parse.matcher(token);

                                    if (m.find())
                                    {
                                        PrintdApplication.get(context)
                                                .setThingiverseService(ThingiverseServiceGenerator
                                                        .createService(ThingiverseService.class, m.group(2) + " " + m.group(1)));
                                    }

                                    context.startActivity(ThingiverseCollectionsActivity.newIntent(context));
                                }
                            }
                        });

            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
                progressVisibility.set(View.INVISIBLE);
            }
        }
    }

    public void resumeActivity() {
        progressVisibility.set(View.INVISIBLE);
    }

    public void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        context.startActivity(launchBrowser);
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
    }

    public void onClickLogin(View view) {
        thingiverseInitLogin();
        octoprintInit();
    }
    public void onClickBedLevel(View view) {
        octoprintInit();
        promptBedLevel();
    }

    public void onClickCreateAccount(View view) {
        context.startActivity(CreateAccountActivity.newIntent(context));
    }

    public void promptBedLevel() {
        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle("Bed level")
                .setMessage("Would you like to start bed leveling?")
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(BedLevelActivity.newIntent(context));
                    }
                })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .show();
    }

    private void octoprintInit() {
        PrintdApplication application = PrintdApplication.get(context);
        //TODO get API key from Heroku
        application.setOctoprintService(OctoprintServiceGenerator.createService(OctoprintService.class, context.getString(R.string.andrews_octoprint_api_secret)));
    }

    public void thingiverseInitLogin() {
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(ThingiverseAuthServiceGenerator.AUTH_BASE_URL + "/login/oauth/authorize" + "?client_id=" + context.getString(R.string.client_id) + "&redirect_uri=" + context.getString(R.string.redirect_uri)));
        context.startActivity(intent);
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }
}
