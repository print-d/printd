package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityBedLevelBinding;
import com.printdinc.printd.databinding.ActivityHelpBinding;
import com.printdinc.printd.databinding.ActivityMainBinding;
import com.printdinc.printd.viewmodel.BedLevelViewModel;
import com.printdinc.printd.viewmodel.HelpViewModel;
import com.printdinc.printd.viewmodel.MainViewModel;

public class HelpActivity extends AppCompatActivity {



    private ActivityHelpBinding binding;
    private HelpViewModel helpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help);
        helpViewModel = new HelpViewModel(this);
        binding.setViewModel(helpViewModel);
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helpViewModel.destroy();
    }


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        return intent;
    }
}
