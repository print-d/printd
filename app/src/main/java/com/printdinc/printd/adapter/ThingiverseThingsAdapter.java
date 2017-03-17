package com.printdinc.printd.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ItemThingBinding;
import com.printdinc.printd.model.ThingiverseCollectionThing;
import com.printdinc.printd.viewmodel.ItemThingViewModel;

import java.util.Collections;
import java.util.List;

public class ThingiverseThingsAdapter extends RecyclerView.Adapter<ThingiverseThingsAdapter.ThingiverseThingsViewHolder> {

    private List<ThingiverseCollectionThing> collectionThings;

    public ThingiverseThingsAdapter() {
        this.collectionThings = Collections.emptyList();
    }

    public ThingiverseThingsAdapter(List<ThingiverseCollectionThing> collectionThings) {
        this.collectionThings = collectionThings;
    }

    public void setCollectionThings(List<ThingiverseCollectionThing> collectionThings) {
        this.collectionThings = collectionThings;
    }

    @Override
    public ThingiverseThingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemThingBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_thing,
                parent,
                false);
        return new ThingiverseThingsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ThingiverseThingsViewHolder holder, int position) {
        holder.bindCollectionThings(collectionThings.get(position));
    }

    @Override
    public int getItemCount() {
        return collectionThings.size();
    }

    public static class ThingiverseThingsViewHolder extends RecyclerView.ViewHolder {
        final ItemThingBinding binding;

        public ThingiverseThingsViewHolder(ItemThingBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void bindCollectionThings(ThingiverseCollectionThing collectionThing) {
            if (binding.getViewModel() == null) {
                binding.setViewModel(new ItemThingViewModel(itemView.getContext(), collectionThing));
            } else {
                binding.getViewModel().setCollection(collectionThing);
            }


        }
    }
}
