package com.printdinc.printd.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityMainBinding;
import com.printdinc.printd.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new MainViewModel(this);
        binding.setViewModel(mainViewModel);
        setSupportActionBar(binding.toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mainViewModel.resumeActivity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        Uri uri = intent.getData();

        String scheme = intent.getScheme();

        mainViewModel.thingiverseLogin(uri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainViewModel.destroy();
    }

}
