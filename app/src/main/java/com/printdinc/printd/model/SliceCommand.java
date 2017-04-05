package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SliceCommand {
    @SerializedName("command")
    @Expose
    private String command;
    @SerializedName("position")
    @Expose
    private Position position;
    @SerializedName("print")
    @Expose
    private Boolean print;

    public String getCommand() {
        return command;
    }

    public Position getPosition() { return position; }

    public Boolean getPrint() { return print; }

    public SliceCommand(String command, Position position, Boolean print) {
        this.command = command;
        this.position = position;
        this.print = print;
    }
}