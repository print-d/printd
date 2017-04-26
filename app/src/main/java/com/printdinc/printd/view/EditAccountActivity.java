package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityEditAccountBinding;
import com.printdinc.printd.viewmodel.EditAccountViewModel;

public class EditAccountActivity extends AppCompatActivity {

    private ActivityEditAccountBinding binding;
    private EditAccountViewModel editAccountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_account);
        editAccountViewModel = new EditAccountViewModel(this, this);
        binding.setViewModel(editAccountViewModel);
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editAccountViewModel.destroy();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, EditAccountActivity.class);
        return intent;
    }
}

