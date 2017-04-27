package com.printdinc.printd.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.model.ConfigFile;
import com.printdinc.printd.model.PrinterProfile;
import com.printdinc.printd.model.User;
import com.printdinc.printd.service.HerokuService;
import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.view.ConfigPrinterActivity;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by andrewthomas on 3/21/17.
 */

public class ItemConfigFileViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = "ItemConfigFileViewModel";

    private ConfigFile file;
    private Context context;
    private ConfigPrinterActivity activity;
    private Subscription subscription;

    private ProgressDialog progressDialog;
    private Handler progressHandler;

    public ItemConfigFileViewModel(ConfigPrinterActivity context, ConfigFile file) {
        this.file = file;
        this.activity = context;
        this.context = context;
    }

    public String getFilename() {
        return this.file.getFilename();
    }

    public void onItemClick(View view) {

        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle("Set Config File")
                .setMessage("Do you want to set this as your default config file?\n" + this.file.getFilename() )
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressHandler = new Handler();
                        progressDialog = new ProgressDialog(context);

                        progressDialog.setTitle("Sending to Printer...");
                        progressDialog.setMessage("Sending to printer...");
                        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
                        progressDialog.setProgress(0);
                        progressDialog.setMax(3);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        changeConfigFile(file.getId());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    private void changeConfigFile(String id) {
        progressDialog.setMessage("Updating your preference...");
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        HerokuService herokuService = application.getHerokuService();
        subscription = herokuService.editUser(new User(null, null, null, null, null, id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error updating Heroku preference", error);
                        errorPrinting();
                    }

                    @Override
                    public void onNext(String s) {

                        progressDialog.incrementProgressBy(1);
                        // Download the config file to be uploaded
                        downloadFile();

                    }
                });
    }

    private void errorPrinting() {
        progressDialog.dismiss();
        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle("Error Printing")
                .setMessage("There was an error uploading.\nMake sure your printer is turned on.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setFile(ConfigFile file) {
        this.file = file;
        notifyChange();
    }

    private void downloadFile() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();

        progressDialog.setMessage("Downloading file...");

        final PrintdApplication application = PrintdApplication.get(context);
        HerokuService herokuService = application.getHerokuService();

        subscription = herokuService.configFile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error printing", error);
                        errorPrinting();
                    }

                    @Override
                    public void onNext(JsonObject obj) {
                        Log.i(TAG, "Got the object" + obj.toString());

                        progressDialog.incrementProgressBy(1);

                        uploadFile(new PrinterProfile("printd", "print(d) profile", true, obj));
                    }
                });
    }

    private void uploadFile(PrinterProfile printerProfile) {
        progressDialog.setMessage("Uploading to OctoPrint...");

        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
        subscription = octoprintService.addSlicingProfile(printerProfile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error uploading printerProfile", error);
                        errorPrinting();
                    }

                    @Override
                    public void onNext(ResponseBody r) {

                        progressDialog.incrementProgressBy(1);

                        if (progressDialog.getProgress() == progressDialog.getMax()) {
                            progressDialog.dismiss();

                            activity.refreshCurrent();

                            new AlertDialog.Builder(context)
                                    .setIcon(0)
                                    .setTitle("Print file")
                                    .setMessage("The file was successfully uploaded!\nThe profile has been saved.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
//                                            Intent newIntent = new Intent(context,MainActivity.class);
//                                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            context.startActivity(newIntent);
                                        }
                                    })
                                    .show();
                        } else {
                            errorPrinting();
                        }

                        Log.i(TAG, "ResponseBody loaded " + r);
                    }
                });


    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
    }
}

