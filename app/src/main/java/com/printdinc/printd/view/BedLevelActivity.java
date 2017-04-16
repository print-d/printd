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
import com.printdinc.printd.databinding.ActivityMainBinding;
import com.printdinc.printd.viewmodel.BedLevelViewModel;
import com.printdinc.printd.viewmodel.MainViewModel;

public class BedLevelActivity extends AppCompatActivity {



    private ActivityBedLevelBinding binding;
    private BedLevelViewModel bedLevelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bed_level);
        bedLevelViewModel = new BedLevelViewModel(this, this);
        binding.setViewModel(bedLevelViewModel);
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bedLevelViewModel.destroy();
    }


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, BedLevelActivity.class);
        return intent;
    }
}
