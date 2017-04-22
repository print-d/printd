package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewthomas on 4/8/17.
 */

public class JobStatus {
    @SerializedName("progress")
    @Expose
    private JobStatusState progress;


    public JobStatusState getProgress() {
        return progress;
    }

    public JobStatus(JobStatusState progress) {
        this.progress = progress;
    }
}