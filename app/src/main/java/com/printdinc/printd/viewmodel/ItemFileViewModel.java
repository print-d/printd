package com.printdinc.printd.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.printdinc.printd.model.ThingiverseThingFile;

/**
 * Created by andrewthomas on 3/21/17.
 */

public class ItemFileViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = "ItemFileViewModel";

    private ThingiverseThingFile thingFile;
    private Context context;

    public ItemFileViewModel(Context context, ThingiverseThingFile thingFile) {
        this.thingFile = thingFile;
        this.context = context;
    }

    public String getName() {
        return this.thingFile.name;
    }

    public void onItemClick(View view) {

        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle("Print file")
                .setMessage("Are you sure you want to print this file?\n" + this.thingFile.name)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with print
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    public String getThumbnailUrl() {
        return thingFile.thumbnail;
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setFile(ThingiverseThingFile thingFile) {
        this.thingFile = thingFile;
        notifyChange();
    }

    @Override
    public void destroy() {
        //In this case destroy doesn't need to do anything because there is not async calls
    }
}

