package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewthomas on 4/8/17.
 */

public class ConnectionState {
    @SerializedName("current")
    @Expose
    private ConnectionStateState current;


    public ConnectionStateState getCurrent() {
        return current;
    }

    public ConnectionState(ConnectionStateState current) {
        this.current = current;
    }
}