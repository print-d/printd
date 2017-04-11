package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PrintHeadCommand {
    @SerializedName("command")
    @Expose
    private String command;
    @SerializedName("axes")
    @Expose
    private ArrayList<String> axes;
    @SerializedName("x")
    @Expose
    private long x;
    @SerializedName("y")
    @Expose
    private long y;
    @SerializedName("z")
    @Expose
    private long z;

    public String getCommand() {
        return command;
    }

    public ArrayList<String> getAxes() { return axes; }

    public long getX() { return x; }

    public long getY() { return y; }

    public long getZ() { return z; }

    public PrintHeadCommand(String command, ArrayList<String> axes, long x, long y, long z) {
        this.command = command;
        this.axes = axes;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}