package com.printdinc.printd.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import com.printdinc.printd.R;
import com.printdinc.printd.adapter.ThingiverseCollectionAdapter;
import com.printdinc.printd.databinding.ActivityThingiverseCollectionsBinding;
import com.printdinc.printd.model.ThingiverseCollection;
import com.printdinc.printd.viewmodel.ThingiverseCollectionsViewModel;

public class ThingiverseCollectionsActivity extends AppCompatActivity implements ThingiverseCollectionsViewModel.DataListener {

    private ActivityThingiverseCollectionsBinding binding;
    private ThingiverseCollectionsViewModel thingiverseCollectionsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thingiverse_collections);
        thingiverseCollectionsViewModel = new ThingiverseCollectionsViewModel(this, this);
        binding.setViewModel(thingiverseCollectionsViewModel);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerView(binding.collectionsRecyclerView);

    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ThingiverseCollectionsActivity.class);
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
        thingiverseCollectionsViewModel.destroy();
    }

    @Override
    public void onCollectionsChanged(List<ThingiverseCollection> collections) {
        ThingiverseCollectionAdapter adapter =
                (ThingiverseCollectionAdapter) binding.collectionsRecyclerView.getAdapter();
        adapter.setCollections(collections);
        adapter.notifyDataSetChanged();
//        hideSoftKeyboard();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ThingiverseCollectionAdapter adapter = new ThingiverseCollectionAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

//    private void hideSoftKeyboard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(binding.editTextUsername.getWindowToken(), 0);
//    }

}
