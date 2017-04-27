package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // This is where the action bar logic goes
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        int x = item.getItemId();

        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;

            case R.id.action_help:
                this.startActivity(HelpActivity.newIntent(this));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, CreateAccountActivity.class);
        return intent;
    }
}