package com.printdinc.printd.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewthomas on 4/25/17.
 */

public class PrinterProfile {

    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("displayName")
    @Expose
    private String displayName;

    @SerializedName("default")
    @Expose
    private boolean is_default;

    @SerializedName("data")
    @Expose
    private JsonObject data;

    public PrinterProfile(String key, String displayName, boolean is_default, JsonObject data) {
        this.key = key;
        this.displayName = displayName;
        this.is_default = is_default;
        this.data = data;
    }
}
