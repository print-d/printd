package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.SimpleCommand;
import com.printdinc.printd.model.User;
import com.printdinc.printd.service.HerokuService;
import com.printdinc.printd.service.HerokuServiceGenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class CreateAccountViewModel implements ViewModel{

    private static final String TAG = "MainViewModel";
    private Activity activity;
    private Context context;
    private Subscription subscription;


    public final ObservableField<String> usernameText = new ObservableField<>("");
    public final ObservableField<String> passwordText = new ObservableField<>("");
    public final ObservableField<String> confirmText = new ObservableField<>("");
    public final ObservableField<String> apiText = new ObservableField<>("");
    public final ObservableArrayList<String> makeEntries = new ObservableArrayList<>();
    public final ObservableArrayList<String> modelEntries = new ObservableArrayList<>();



    public CreateAccountViewModel(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        


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

           User newUser = new User(usernameText.get().toString(), passwordText.get().toString(), apiText.get().toString(), "Wanhao", "Duplicator i3");
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
                   new AlertDialog.Builder(context)
                           .setIcon(0)
                           .setTitle("Account Created")
                           .setMessage(r)
                           .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // Do nothing
                                   activity.finish();
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
           });
       }
       else {
           new AlertDialog.Builder(context)
                   .setIcon(0)
                   .setTitle("Account Created")
                   .setMessage("Are you finished?")
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
