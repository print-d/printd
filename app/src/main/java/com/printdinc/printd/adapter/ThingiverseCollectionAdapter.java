package com.printdinc.printd.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ItemCollectionBinding;
import com.printdinc.printd.model.ThingiverseCollection;
import com.printdinc.printd.viewmodel.ItemCollectionViewModel;

public class ThingiverseCollectionAdapter extends RecyclerView.Adapter<ThingiverseCollectionAdapter.ThingiverseCollectionViewHolder> {

    private List<ThingiverseCollection> collections;

    public ThingiverseCollectionAdapter() {
        this.collections = Collections.emptyList();
    }

    public ThingiverseCollectionAdapter(List<ThingiverseCollection> collections) {
        this.collections = collections;
    }

    public void setCollections(List<ThingiverseCollection> collections) {
        this.collections = collections;
    }

    @Override
    public ThingiverseCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCollectionBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_collection,
                parent,
                false);
        return new ThingiverseCollectionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ThingiverseCollectionViewHolder holder, int position) {
        holder.bindCollection(collections.get(position));
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public static class ThingiverseCollectionViewHolder extends RecyclerView.ViewHolder {
        final ItemCollectionBinding binding;

        public ThingiverseCollectionViewHolder(ItemCollectionBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void bindCollection(ThingiverseCollection collection) {
            if (binding.getViewModel() == null) {
                binding.setViewModel(new ItemCollectionViewModel(itemView.getContext(), collection));
            } else {
                binding.getViewModel().setCollection(collection);
            }


        }
    }
}
