package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityBedLevelBinding;
import com.printdinc.printd.databinding.ActivityHelpBinding;
import com.printdinc.printd.databinding.ActivityPrintStatusBinding;
import com.printdinc.printd.databinding.ActivityMainBinding;
import com.printdinc.printd.viewmodel.BedLevelViewModel;
import com.printdinc.printd.viewmodel.HelpViewModel;
import com.printdinc.printd.viewmodel.MainViewModel;
import com.printdinc.printd.viewmodel.PrintStatusViewModel;

public class PrintStatusActivity extends AppCompatActivity {



    private ActivityPrintStatusBinding binding;
    private PrintStatusViewModel printStatusViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_print_status);
        printStatusViewModel = new PrintStatusViewModel(this, this);
        binding.setViewModel(printStatusViewModel);
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printStatusViewModel.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_print_status, menu);
        return true;
    }

    // This is where the action bar logic goes
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        int x = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_refresh:
                // Somehow make it reload here.
                // User chose the "Settings" item, show the app settings UI...
                printStatusViewModel.getJobStatus();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PrintStatusActivity.class);
        return intent;
    }
}
