package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.model.Login;
import com.printdinc.printd.model.User;
import com.printdinc.printd.service.HerokuService;
import com.printdinc.printd.service.HerokuServiceGenerator;
import com.printdinc.printd.view.CreateAccountActivity;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by pdixon on 4/24/2017.
 */

public class LoginViewModel implements ViewModel {

    private static final String TAG = "MainViewModel";
    private Activity activity;
    private Context context;
    private Subscription subscription;

    public final ObservableField<String> usernameText = new ObservableField<>("");
    public final ObservableField<String> passwordText = new ObservableField<>("");

    public LoginViewModel(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;

    }

    public void onClickLogin (View view) {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        HerokuService herokuService = application.getHerokuService();

        //User newUser = new User(usernameText.get().toString(), passwordText.get().toString(), apiText.get().toString(), makeEntries.get(selectedMakePosition.get()), modelEntries.get(selectedModelPosition.get()));
        Login newLogin = new Login(usernameText.get().toString(), passwordText.get().toString());
        subscription = herokuService.login(newLogin)
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

    public void onClickCreateAccount(View view) {
        context.startActivity(CreateAccountActivity.newIntent(context));
        activity.finish();
    }
}
