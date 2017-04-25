package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.JobStatus;
import com.printdinc.printd.model.JobStatusState;
import com.printdinc.printd.model.NewNSDInfo;
import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.service.OctoprintServiceGenerator;
import com.printdinc.printd.service.ThingiverseAuthService;
import com.printdinc.printd.service.ThingiverseAuthServiceGenerator;
import com.printdinc.printd.service.ThingiverseService;
import com.printdinc.printd.service.ThingiverseServiceGenerator;
import com.printdinc.printd.view.BedLevelActivity;
import com.printdinc.printd.view.PrintStatusActivity;
import com.printdinc.printd.view.CreateAccountActivity;
import com.printdinc.printd.view.LoginActivity;
import com.printdinc.printd.view.ThingiverseCollectionsActivity;

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

    public ArrayAdapter<NewNSDInfo> services;
    private int mSelectedItem = -1;

    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdManager mNsdManager;

    public static final String SERVICE_TYPE = "_http._tcp.";

    Pattern token_parse = Pattern.compile("access_token=(.+?)&token_type=(\\w+)");


    public MainViewModel(Context context) {
        this.context = context;
        progressVisibility = new ObservableInt(View.INVISIBLE);

        services = new ArrayAdapter<NewNSDInfo>(context, android.R.layout.select_dialog_singlechoice);

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        initializeNsd();
        discoverServices();
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
        promptBedLevel();
    }


    public void onClickLoginAccount(View view) {
        context.startActivity(LoginActivity.newIntent(context));
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
//            services.clear();


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select OctoPrint instance");
//            builder.setMessage("Select an OctoPrint instance from the choices below. If yours does not appear, go to http://octoprint.org/download/");
            builder.setSingleChoiceItems(services, -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                mSelectedItem = which;
                        }
                    });
            builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button

                    if (mSelectedItem != -1) {
                        stopDiscovery();
                        NewNSDInfo s = services.getItem(mSelectedItem);
                        String url = "http://" + s.getBaseUrl() + "/";
                        PrintdApplication application = PrintdApplication.get(context);
                        application.setOctoprintService(OctoprintServiceGenerator.createService(OctoprintService.class, url, context.getString(R.string.andrews_octoprint_api_secret)));
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

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (service.getServiceName().contains("")) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".

//                    new AddNsdServiceInfoTask().execute(new NewNSDInfo(service));
//                        services.add(service);

                    mNsdManager.resolveService(service, mResolveListener);


                    Log.d(TAG, "Name: " + service.getServiceName());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);

//                services.remove(service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);


                new AddNsdServiceInfoTask().execute(new NewNSDInfo(serviceInfo));
            }
        };
    }

    public void initializeNsd() {
        initializeDiscoveryListener();
        initializeResolveListener();

    }

    public void discoverServices() {
        try {
            mNsdManager.discoverServices(
                    SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        }
        catch (Exception error) {
            System.out.println(error);
        }

    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    class AddNsdServiceInfoTask extends AsyncTask<NewNSDInfo, NewNSDInfo, Void> {
        @Override
        protected Void doInBackground(NewNSDInfo... params) {
            for (NewNSDInfo item : params) {
                publishProgress(item);
            }

            return null;
        }

        @SuppressWarnings("unchecked")
        protected void onProgressUpdate(NewNSDInfo... item) {
            for (int i = 0; i < services.getCount(); i++) {
                if (services.getItem(i).toString().equals(item.toString()) )
                {
                    return;
                }
            }
            services.add(item[0]);
        }

    }
}

