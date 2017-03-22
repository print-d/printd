package com.printdinc.printd.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ItemFileBinding;
import com.printdinc.printd.model.ThingiverseThingFile;
import com.printdinc.printd.viewmodel.ItemFileViewModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by andrewthomas on 3/21/17.
 */

public class ThingiverseFilesAdapter extends RecyclerView.Adapter<ThingiverseFilesAdapter.ThingiverseFilesViewHolder> {

    private List<ThingiverseThingFile> collectionFiles;

    public ThingiverseFilesAdapter() {
        this.collectionFiles = Collections.emptyList();
    }

    public ThingiverseFilesAdapter(List<ThingiverseThingFile> collectionFiles) {
        this.collectionFiles = collectionFiles;
    }

    public void setCollectionFiles(List<ThingiverseThingFile> collectionFiles) {
        this.collectionFiles = collectionFiles;
    }

    @Override
    public ThingiverseFilesAdapter.ThingiverseFilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFileBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_file,
                parent,
                false);
        return new ThingiverseFilesAdapter.ThingiverseFilesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ThingiverseFilesAdapter.ThingiverseFilesViewHolder holder, int position) {
        holder.bindFiles(collectionFiles.get(position));
    }

    @Override
    public int getItemCount() {
        return collectionFiles.size();
    }

    public static class ThingiverseFilesViewHolder extends RecyclerView.ViewHolder {
        final ItemFileBinding binding;

        public ThingiverseFilesViewHolder(ItemFileBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void bindFiles(ThingiverseThingFile thingFile) {
            if (binding.getViewModel() == null) {
                binding.setViewModel(new ItemFileViewModel(itemView.getContext(), thingFile));
            } else {
                binding.getViewModel().setFile(thingFile);
            }


        }
    }
}
