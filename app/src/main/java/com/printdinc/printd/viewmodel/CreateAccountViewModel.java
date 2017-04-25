package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.Printer;
import com.printdinc.printd.model.PrinterData;
import com.printdinc.printd.model.User;
import com.printdinc.printd.service.HerokuService;
import com.printdinc.printd.service.HerokuServiceGenerator;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class CreateAccountViewModel implements ViewModel{

    private static final String TAG = "MainViewModel";
    private Activity activity;
    private Context context;
    private Subscription subscription;
    private List<Printer> printerList;


    public final ObservableField<String> usernameText = new ObservableField<>("");
    public final ObservableField<String> passwordText = new ObservableField<>("");
    public final ObservableField<String> confirmText = new ObservableField<>("");
    public final ObservableField<String> apiText = new ObservableField<>("");
    public final ObservableArrayList<String> makeEntries = new ObservableArrayList<>();
    public final ObservableArrayList<String> modelEntries = new ObservableArrayList<>();
    public final ObservableInt selectedMakePosition = new ObservableInt(0);
    public final ObservableInt selectedModelPosition = new ObservableInt(0);



    public CreateAccountViewModel(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

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
                            makeEntries.add(currPrinter.getMake());
                            modelEntries.add(currPrinter.getModel());
                        }
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

            User newUser = new User(usernameText.get().toString(), passwordText.get().toString(), apiText.get().toString(), makeEntries.get(selectedMakePosition.get()), modelEntries.get(selectedModelPosition.get()));
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


}
