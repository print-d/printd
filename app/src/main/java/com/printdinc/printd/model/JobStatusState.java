package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewthomas on 4/8/17.
 */

public class JobStatusState {
    @SerializedName("completion")
    @Expose
    private double completion;

    @SerializedName("filepos")
    @Expose
    private int filepos;

    @SerializedName("printTime")
    @Expose
    private int printTime;

    @SerializedName("printTimeLeft")
    @Expose
    private int printTimeLeft;


    public double getCompletion() {
        return completion;
    }

    public int getFilepos() {
        return filepos;
    }

    public int getPrintTime() {
        return printTime;
    }

    public int getPrintTimeLeft() {
        return printTimeLeft;
    }

    public JobStatusState(double completion, int filepos, int printTime, int printTimeLeft) {
        this.completion = completion;
        this.filepos = filepos;
        this.printTime = printTime;
        this.printTimeLeft = printTimeLeft;
    }
}