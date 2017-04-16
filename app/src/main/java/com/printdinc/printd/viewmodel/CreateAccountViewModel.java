package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.printdinc.printd.R;
import com.printdinc.printd.model.ThingiverseCollection;

public class CreateAccountViewModel implements ViewModel{

    private Context context;


    public String usernameText;

    public CreateAccountViewModel(Context context) {
        this.context = context;
        usernameText = "Test";
    }

    @Override
    public void destroy() {

    }

}
