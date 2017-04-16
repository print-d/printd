package com.printdinc.printd.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.printdinc.printd.R;

/**
 * Created by pdixon on 4/16/2017.
 */

public class CreateAccountActivity extends AppCompatActivity {

    private Button completeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }
}