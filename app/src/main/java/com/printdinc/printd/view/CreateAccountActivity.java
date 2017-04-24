package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityCreateAccountBinding;
import com.printdinc.printd.viewmodel.CreateAccountViewModel;

/**
 * Created by pdixon on 4/16/2017.
 */

public class CreateAccountActivity extends AppCompatActivity {


    private ActivityCreateAccountBinding binding;
    private CreateAccountViewModel createAccountViewModel;
    private Button completeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account);
        createAccountViewModel = new CreateAccountViewModel(this, this);
        binding.setViewModel(createAccountViewModel);
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        createAccountViewModel.destroy();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, CreateAccountActivity.class);
        return intent;
    }
}