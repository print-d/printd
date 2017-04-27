package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.printdinc.printd.R;
import com.printdinc.printd.adapter.ConfigFilesAdapter;
import com.printdinc.printd.databinding.ActivityConfigPrinterBinding;
import com.printdinc.printd.model.ConfigFile;
import com.printdinc.printd.viewmodel.ConfigPrinterViewModel;

import java.util.List;

public class ConfigPrinterActivity extends AppCompatActivity implements ConfigPrinterViewModel.DataListener {

    private ActivityConfigPrinterBinding binding;
    private ConfigPrinterViewModel configPrinterViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config_printer);
        configPrinterViewModel = new ConfigPrinterViewModel(this, this);
        binding.setViewModel(configPrinterViewModel);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerView(binding.configFilesRecyclerView);

    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ConfigPrinterActivity.class);
        return intent;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//
//        Uri uri = getIntent().getData();
//
//        thingiverseCollectionsViewModel.thingiverseLogin(uri);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        configPrinterViewModel.destroy();
    }

    @Override
    public void onConfigFilesChanged(List<ConfigFile> files) {
        ConfigFilesAdapter adapter =
                (ConfigFilesAdapter) binding.configFilesRecyclerView.getAdapter();
        adapter.setConfigFiles(files);
        adapter.notifyDataSetChanged();
//        hideSoftKeyboard();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ConfigFilesAdapter adapter = new ConfigFilesAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

//    private void hideSoftKeyboard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(binding.editTextUsername.getWindowToken(), 0);
//    }

}
