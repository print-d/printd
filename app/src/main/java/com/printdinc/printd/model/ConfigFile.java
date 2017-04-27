package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewthomas on 4/25/17.
 */

public class ConfigFile {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("filename")
    @Expose
    private String filename;

    public String getId() {
        return id;
    }
    public String getFilename() { return filename; }
}
