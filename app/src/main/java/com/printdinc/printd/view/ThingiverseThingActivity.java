package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityThingiverseThingBinding;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.viewmodel.ThingiverseThingViewModel;

public class ThingiverseThingActivity extends AppCompatActivity {

    private static final String EXTRA_THING = "EXTRA_THING";

    private ActivityThingiverseThingBinding binding;
    private ThingiverseThingViewModel thingiverseThingViewModel;

    public static Intent newIntent(Context context, ThingiverseCollectionThing thing) {
        Intent intent = new Intent(context, ThingiverseThingActivity.class);
        intent.putExtra(EXTRA_THING, thing);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thingiverse_thing);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ThingiverseCollectionThing thing = getIntent().getParcelableExtra(EXTRA_THING);
        thingiverseThingViewModel = new ThingiverseThingViewModel(this, thing);
        binding.setViewModel(thingiverseThingViewModel);

        //Currently there is no way of setting an activity title using data binding
        setTitle(thing.name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thingiverseThingViewModel.destroy();
    }
}
