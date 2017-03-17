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
import com.printdinc.printd.adapter.ThingiverseThingsAdapter;
import com.printdinc.printd.databinding.ActivityThingiverseCollectionBinding;
import com.printdinc.printd.model.ThingiverseCollection;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.viewmodel.ThingiverseCollectionViewModel;

import java.util.List;

public class ThingiverseCollectionActivity extends AppCompatActivity implements ThingiverseCollectionViewModel.DataListener {

    private ThingiverseCollection collection;

    private static final String EXTRA_COLLECTION = "EXTRA_COLLECTION";

    private ActivityThingiverseCollectionBinding binding;
    private ThingiverseCollectionViewModel thingiverseCollectionViewModel;

    public static Intent newIntent(Context context, ThingiverseCollection collection) {
        Intent intent = new Intent(context, ThingiverseCollectionActivity.class);
        intent.putExtra(EXTRA_COLLECTION, collection);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (collection == null) collection = getIntent().getParcelableExtra(EXTRA_COLLECTION);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_thingiverse_collection);
        thingiverseCollectionViewModel = new ThingiverseCollectionViewModel(this, this, collection);
        binding.setViewModel(thingiverseCollectionViewModel);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerView(binding.collectionsRecyclerView);

        setTitle(collection.name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thingiverseCollectionViewModel.destroy();
    }

    @Override
    public void onCollectionsChanged(List<ThingiverseCollectionThing> collectionThings) {
        ThingiverseThingsAdapter adapter =
                (ThingiverseThingsAdapter) binding.collectionsRecyclerView.getAdapter();
        adapter.setCollectionThings(collectionThings);
        adapter.notifyDataSetChanged();
//        hideSoftKeyboard();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ThingiverseThingsAdapter adapter = new ThingiverseThingsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
