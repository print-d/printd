package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PrintHeadCommand {

    // Mandatory. The two commands we need are 'home' and 'jog'. See Octoprint API for more detail.
    @SerializedName("command")
    @Expose
    private String command;

    // Only need this for the home command. Just make an arraylist like this: new ArrayList<String>(Arrays.asList("x", "y", "z"))
    @SerializedName("axes")
    @Expose
    private ArrayList<String> axes;

    // Optional. Amount/coordinate to jog print head on x axis, must be a valid number corresponding to the distance to travel in mm.
    @SerializedName("x")
    @Expose
    private long x;

    // Optional. Amount/coordinate to jog print head on y axis, must be a valid number corresponding to the distance to travel in mm.
    @SerializedName("y")
    @Expose
    private long y;

    // Optional. Amount/coordinate to jog print head on z axis, must be a valid number corresponding to the distance to travel in mm.
    @SerializedName("z")
    @Expose
    private long z;

    @SerializedName("absolute")
    @Expose
    private boolean absolute;

    public String getCommand() {
        return command;
    }

    public ArrayList<String> getAxes() { return axes; }

    public long getX() { return x; }

    public long getY() { return y; }

    public long getZ() { return z; }

    public boolean getAbsolute() { return absolute; }

    public PrintHeadCommand(String command, ArrayList<String> axes, long x, long y, long z, boolean absolute) {
        this.command = command;
        this.axes = axes;
        this.x = x;
        this.y = y;
        this.z = z;
        this.absolute = absolute;
    }
}