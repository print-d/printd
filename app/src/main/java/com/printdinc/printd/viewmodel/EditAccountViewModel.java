package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.NewNSDInfo;
import com.printdinc.printd.model.Printer;
import com.printdinc.printd.model.PrinterData;
import com.printdinc.printd.model.User;
import com.printdinc.printd.service.HerokuService;
import com.printdinc.printd.service.HerokuServiceGenerator;
import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.service.OctoprintServiceGenerator;
import com.printdinc.printd.view.ConfigPrinterActivity;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by pdixon on 4/25/2017.
 */

public class EditAccountViewModel implements ViewModel{

    private static final String TAG = "MainViewModel";
    private Activity activity;
    private Context context;
    private Subscription subscription;
    private Subscription subscription2;
    private List<Printer> printerList;


    public final ObservableField<String> usernameText = new ObservableField<>("");
    public final ObservableField<String> passwordText = new ObservableField<>("");
    public final ObservableField<String> confirmText = new ObservableField<>("");
    public final ObservableField<String> apiText = new ObservableField<>("");
    public final ObservableArrayList<String> makeEntries2 = new ObservableArrayList<>();
    public final ObservableArrayList<String> modelEntries2 = new ObservableArrayList<>();
    public final ObservableInt selectedMakePosition2 = new ObservableInt(0);
    public final ObservableInt selectedModelPosition2 = new ObservableInt(0);

    private boolean discoveringServices;

    public ArrayAdapter<NewNSDInfo> services;
    private int mSelectedItem = -1;

    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdManager mNsdManager;

    public static final String SERVICE_TYPE = "_http._tcp.";



    public EditAccountViewModel(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        services = new ArrayAdapter<NewNSDInfo>(context, android.R.layout.select_dialog_singlechoice);

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        discoveringServices = false;

        initializeNsd();
        if (!(discoveringServices)) {
            discoverServices();
            discoveringServices = true;
        }

        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        HerokuService herokuService = application.getHerokuService();

        subscription = herokuService.printerData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<PrinterData>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError (Throwable error){
                        Log.e(TAG, "Error getting printerdata", error);

                    }

                    @Override
                    public void onNext(PrinterData printer_data) {
                        Log.i(TAG, "ResponseBody loaded");

                        printerList = printer_data.getPrinters();
                        for (int i = 0; i < printerList.size(); i++) {
                            Printer currPrinter = printerList.get(i);
                            makeEntries2.add(currPrinter.getMake());
                            modelEntries2.add(currPrinter.getModel());
                        }

                        PrintdApplication application = PrintdApplication.get(EditAccountViewModel.this.context);
                        HerokuService herokuService = application.getHerokuService();
                        subscription2 = herokuService.getUser()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(application.defaultSubscribeScheduler())
                                .subscribe(new Subscriber<User>() {
                                    @Override
                                    public void onCompleted() {
                                        Log.e(TAG, "completed!");
                                    }

                                    @Override
                                    public void onError (Throwable error){
                                        Log.e(TAG, "Error getting userdata", error);

                                    }

                                    @Override
                                    public void onNext(User user) {
                                        Log.i(TAG, "ResponseBody loaded");
                                        apiText.set(user.getOP_APIKey());
                                        selectedMakePosition2.set(printerList.indexOf(user.getMake()));
                                        selectedModelPosition2.set(printerList.indexOf(user.getModel()));


                                    }
                                });
                    }
                });


    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;

    }



    public void onClickComplete (View view) {
        if (passwordText.get().toString().equals(confirmText.get().toString())) {
            if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
            PrintdApplication application = PrintdApplication.get(context);
            HerokuService herokuService = application.getHerokuService();

            User newUser = new User(usernameText.get().toString(), passwordText.get().toString(), apiText.get().toString(), makeEntries2.get(selectedMakePosition2.get()), modelEntries2.get(selectedModelPosition2.get()), null);
            subscription = herokuService.createUser(newUser)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            Log.e(TAG, "completed!");
                        }

                        @Override
                        public void onError (Throwable error){
                            Log.e(TAG, "Error creating account", error);

                        }

                        @Override
                        public void onNext(String r) {
                            Log.i(TAG, "ResponseBody loaded " + r);

                            PrintdApplication application = PrintdApplication.get(context);
                            application.setHerokuService(HerokuServiceGenerator.createService(HerokuService.class, r));
                            activity.finish();
                        }
                    });
        }
        else {
            new AlertDialog.Builder(context)
                    .setIcon(0)
                    .setTitle("Password Error")
                    .setMessage("Close account creation?")
                    .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Go back here
                            activity.finish();

                        }
                    })
                    .show();
        }
    }

    public void onClickConfigPrinter(View view) {
        octoprintInit(ConfigPrinterActivity.newIntent(context));
    }

    private void octoprintInit(final Intent intentToCall) {
        PrintdApplication application = PrintdApplication.get(context);
        //TODO get API key from Heroku
        if (application.getOctoprintService() == null)
        {
            HerokuService herokuService = application.getHerokuService();



            if (!(discoveringServices)) {
                services.clear();
                discoverServices();
                discoveringServices = true;
            }

            int countOctopis = 0;
            int octopiIndex = -1;

            for (int i = 0; i < services.getCount(); i++) {
                if (services.getItem(i).toString().contains("OctoPrint")) {
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
                            }

                            @Override
                            public void onNext(User data) {
                                Log.i(TAG, "User Data received " + data.getOP_APIKey());
                                if (data != null)
                                {
                                    stopDiscovery();
                                    NewNSDInfo s = services.getItem(lastOctopiIndex);
                                    String url = "http://" + s.getBaseUrl() + "/";
                                    PrintdApplication application = PrintdApplication.get(context);
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
                            final int lastSelectedItem = mSelectedItem;
                            PrintdApplication application = PrintdApplication.get(context);
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
                                        }

                                        @Override
                                        public void onNext(User data) {
                                            Log.i(TAG, "Token received " + data.getOP_APIKey());
                                            if (data != null)
                                            {
                                                stopDiscovery();
                                                NewNSDInfo s = services.getItem(lastSelectedItem);
                                                String url = "http://" + s.getBaseUrl() + "/";
                                                PrintdApplication application = PrintdApplication.get(context);
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
                            PrintdApplication application = PrintdApplication.get(context);
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

                // The name of the service tells the user what they'd be
                // connecting to. It could be "Bob's Chat App".

//                    new AddNsdServiceInfoTask().execute(new NewNSDInfo(service));
//                        services.add(service);

                mNsdManager.resolveService(service, mResolveListener);


                Log.d(TAG, "Name: " + service.getServiceName());
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


                // Have to use an async task to avoid setting something on a thread that doesn't own it
                new EditAccountViewModel.AddNsdServiceInfoTask().execute(new NewNSDInfo(serviceInfo));
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
            discoveringServices = true;
        }
        catch (Exception error) {
            System.out.println(error);
        }

    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        discoveringServices = false;
    }


    // A subclass is used so that the services list is accessable
    class AddNsdServiceInfoTask extends AsyncTask<NewNSDInfo, NewNSDInfo, Void> {
        @Override
        protected Void doInBackground(NewNSDInfo... params) {
            for (NewNSDInfo item : params) {
                publishProgress(item);
            }

            return null;
        }

        //@SuppressWarnings("unchecked")
        protected void onProgressUpdate(NewNSDInfo... item) {
            Log.i(TAG, "onProgressUpdate: " + item[0].toString());
            for (int i = 0; i < services.getCount(); i++) {
                if (services.getItem(i).toString() != null &&
                        services.getItem(i).toString().equals(item[0].toString()) )
                {
                    return;
                }
            }
            services.add(item[0]);
        }

    }


}
