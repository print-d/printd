package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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
    public User originalUser;


    public final ObservableField<String> usernameText = new ObservableField<>("");
    public final ObservableField<String> passwordText = new ObservableField<>("");
    public final ObservableField<String> confirmText = new ObservableField<>("");
    public final ObservableField<String> apiText = new ObservableField<>("");
    public final ObservableArrayList<String> makeEntries2 = new ObservableArrayList<>();
    public final ObservableArrayList<String> modelEntries2 = new ObservableArrayList<>();
    public final ObservableInt selectedMakePosition2 = new ObservableInt(0);
    public final ObservableInt selectedModelPosition2 = new ObservableInt(0);



    public EditAccountViewModel(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        PrintdApplication application = PrintdApplication.get(context);
        if (application.getOctoprintService() == null)
            application.discoverServices();

        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
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
        final User newUser = new User(null, null, null, null, null, null);

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
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error getting userdata", error);

                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(TAG, "ResponseBody loaded");

                        if (!user.getOP_APIKey().toString().equals(apiText.get().toString())) {
                            newUser.setOp_apikey(apiText.get().toString());
                        }
                        if (!user.getModel().toString().equals(modelEntries2.get(selectedModelPosition2.get()))) {
                            newUser.setModel(modelEntries2.get(selectedModelPosition2.get()));
                            if (!user.getMake().toString().equals(makeEntries2.get(selectedMakePosition2.get()))) {
                                newUser.setMake(makeEntries2.get(selectedMakePosition2.get()));
                            }
                        }
                        if (passwordText.get().toString().equals(confirmText.get().toString()) && !passwordText.get().equals("")) {
                            newUser.setPassword(passwordText.get().toString());
                        }

                        if (subscription != null && !subscription.isUnsubscribed())
                            subscription.unsubscribe();
                        PrintdApplication application = PrintdApplication.get(context);
                        HerokuService herokuService = application.getHerokuService();

                        subscription = herokuService.editUser(newUser)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(application.defaultSubscribeScheduler())
                                .subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onCompleted() {
                                        Log.e(TAG, "completed!");
                                    }

                                    @Override
                                    public void onError(Throwable error) {
                                        Log.e(TAG, "Error creating account", error);

                                    }

                                    @Override
                                    public void onNext(String r) {
                                        Log.i(TAG, "ResponseBody loaded " + r);
                                        activity.finish();
                                    }
                                });
                    }

                });
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


}
