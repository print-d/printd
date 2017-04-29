package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.NewNSDInfo;
import com.printdinc.printd.model.User;
import com.printdinc.printd.service.HerokuService;
import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.service.OctoprintServiceGenerator;
import com.printdinc.printd.service.ThingiverseAuthServiceGenerator;
import com.printdinc.printd.service.ThingiverseService;
import com.printdinc.printd.service.ThingiverseServiceGenerator;
import com.printdinc.printd.view.BedLevelActivity;
import com.printdinc.printd.view.PrintStatusActivity;
import com.printdinc.printd.view.ThingiverseCollectionsActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    Pattern token_parse = Pattern.compile("(.+?)&token_type=?(\\w*)");


    public MainViewModel(Context context) {
        this.context = context;
        progressVisibility = new ObservableInt(View.INVISIBLE);

        PrintdApplication application = PrintdApplication.get(context);
        if (application.getOctoprintService() == null)
            application.discoverServices();

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
                HerokuService herokuService = application.getHerokuService();

                Map<String, String> m = new HashMap<String, String>();
                m.put("code", code);

                //TODO reshape this call to be to Heroku server instead of thingiverse
                subscription = herokuService.authenticateThingiverse(m)
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
                                                        .createService(ThingiverseService.class, "Bearer" + " " + m.group(1)));
                                    }

                                    octoprintInit(ThingiverseCollectionsActivity.newIntent(context));

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
    }
    public void onClickBedLevel(View view) {
        octoprintInit(BedLevelActivity.newIntent(context));
    }


    public void onClickCheckPrintStatus(View view) {
        octoprintInit(PrintStatusActivity.newIntent(context));
    }

    public void promptBedLevel() {
        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle("Bed level")
                .setMessage("Would you like to start bed leveling?")
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        octoprintInit(BedLevelActivity.newIntent(context));
                    }
                })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .show();
    }

    private void octoprintInit(final Intent intentToCall) {
        PrintdApplication application = PrintdApplication.get(context);
        //TODO get API key from Heroku
        if (application.getOctoprintService() == null)
        {
            HerokuService herokuService = application.getHerokuService();



            application.discoverServices();

            int countOctopis = 0;
            int octopiIndex = -1;

            for (int i = 0; i < application.getServices().getCount(); i++) {
                if (application.getServices().getItem(i).toString().contains("OctoPrint")) {
                    countOctopis++;
                    octopiIndex = i;
                }
            }

            final int lastOctopiIndex = octopiIndex;

            if (countOctopis == 1) {
                Log.i(TAG, "Starting getting user data");
                herokuService.getUserData()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(application.defaultSubscribeScheduler())
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable error) {
                                Log.e(TAG, "Error receiving user data ", error);
                                progressVisibility.set(View.INVISIBLE);
                            }

                            @Override
                            public void onNext(User data) {
                                Log.i(TAG, "User Data received " + data.getOP_APIKey());
                                if (data != null)
                                {
                                    PrintdApplication application = PrintdApplication.get(context);
                                    application.stopDiscovery();
                                    NewNSDInfo s = application.getServices().getItem(lastOctopiIndex);
                                    String url = "http://" + s.getBaseUrl() + "/";
                                    application.setOctoprintService(OctoprintServiceGenerator.createService(OctoprintService.class, url, data.getOP_APIKey()));
                                    if (intentToCall != null) {
                                        context.startActivity(intentToCall);
                                    }

                                }
                            }
                        });

                return;
            }
            else {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select OctoPrint instance");
//            builder.setMessage("Select an OctoPrint instance from the choices below. If yours does not appear, go to http://octoprint.org/download/");
                builder.setSingleChoiceItems(application.getServices(), -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PrintdApplication application = PrintdApplication.get(context);
                                application.mSelectedItem = which;
                            }
                        });
                builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        PrintdApplication application = PrintdApplication.get(context);
                        if (application.mSelectedItem != -1) {
                            final int lastSelectedItem = application.mSelectedItem;
                            HerokuService herokuService = application.getHerokuService();
                            herokuService.getUserData()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(application.defaultSubscribeScheduler())
                                    .subscribe(new Subscriber<User>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable error) {
                                            Log.e(TAG, "Error receiving token ", error);
                                            progressVisibility.set(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onNext(User data) {
                                            Log.i(TAG, "Token received " + data.getOP_APIKey());
                                            if (data != null)
                                            {
                                                PrintdApplication application = PrintdApplication.get(context);
                                                application.stopDiscovery();
                                                NewNSDInfo s = application.getServices().getItem(lastSelectedItem);
                                                String url = "http://" + s.getBaseUrl() + "/";
                                                application.setOctoprintService(OctoprintServiceGenerator.createService(OctoprintService.class, url, data.getOP_APIKey()));
                                                if (intentToCall != null) {
                                                    context.startActivity(intentToCall);
                                                }

                                            }
                                        }
                                    });
                        }

                        // TODO remove this for "release" distribution
                        else {
                            String url = "http://localhost/";
                            application.setOctoprintService(OctoprintServiceGenerator.createService(OctoprintService.class, url, ""));
                            Toast.makeText(context, "This is for demonstration purposes only!!", Toast.LENGTH_LONG).show();

                            if (intentToCall != null) {
                                context.startActivity(intentToCall);
                            }
                        }

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else
        {
            if (intentToCall != null) {
                context.startActivity(intentToCall);
            }
        }

    }

    public void thingiverseInitLogin() {
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(ThingiverseAuthServiceGenerator.AUTH_BASE_URL + "/login/oauth/authorize" + "?client_id=" + context.getString(R.string.client_id) + "&redirect_uri=" + context.getString(R.string.redirect_uri)));
        context.startActivity(intent);
    }

    public void goToThingiverse (View view) {
        goToUrl ( context.getString(R.string.thingiverseURI));
    }
}

