package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewthomas on 4/8/17.
 */

public class SimpleCommand {
    @SerializedName("command")
    @Expose
    private String command;

    public String getCommand() {
        return command;
    }

    public SimpleCommand(String command) {
        this.command = command;
    }
}