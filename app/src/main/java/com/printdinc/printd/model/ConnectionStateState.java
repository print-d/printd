package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewthomas on 4/8/17.
 */

public class ConnectionStateState {
    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("port")
    @Expose
    private String port;

    @SerializedName("baudrate")
    @Expose
    private String baudrate;

    @SerializedName("printerProfile")
    @Expose
    private String printerProfile;


    public String getState() {
        return state;
    }

    public String getPort() {
        return port;
    }

    public String getBaudrate() {
        return baudrate;
    }

    public String getPrinterProfile() {
        return printerProfile;
    }

    public ConnectionStateState(String state, String port, String baudrate, String printerProfile) {
        this.state = state;
        this.port = port;
        this.baudrate = baudrate;
        this.printerProfile = printerProfile;
    }
}