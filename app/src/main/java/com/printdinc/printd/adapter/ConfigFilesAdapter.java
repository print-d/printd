package com.printdinc.printd.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ItemConfigFileBinding;
import com.printdinc.printd.model.ConfigFile;
import com.printdinc.printd.view.ConfigPrinterActivity;
import com.printdinc.printd.viewmodel.ItemConfigFileViewModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by andrewthomas on 3/21/17.
 */

public class ConfigFilesAdapter extends RecyclerView.Adapter<ConfigFilesAdapter.ConfigFilesViewHolder> {

    private List<ConfigFile> files;

    public ConfigFilesAdapter() {
        this.files = Collections.emptyList();
    }

    public ConfigFilesAdapter(List<ConfigFile> files) {
        this.files = files;
    }

    public void setConfigFiles(List<ConfigFile> files) {
        this.files = files;
    }

    @Override
    public ConfigFilesAdapter.ConfigFilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemConfigFileBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_config_file,
                parent,
                false);
        return new ConfigFilesAdapter.ConfigFilesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ConfigFilesAdapter.ConfigFilesViewHolder holder, int position) {
        holder.bindFiles(files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class ConfigFilesViewHolder extends RecyclerView.ViewHolder {
        final ItemConfigFileBinding binding;

        public ConfigFilesViewHolder(ItemConfigFileBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void bindFiles(ConfigFile file) {
            if (binding.getViewModel() == null) {
                binding.setViewModel(new ItemConfigFileViewModel((ConfigPrinterActivity) itemView.getContext(), file));
            } else {
                binding.getViewModel().setFile(file);
            }


        }
    }
}
