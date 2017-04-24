package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pdixon on 4/16/2017.
 */

public class Printer {
    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Make")
    @Expose
    private String make;
    @SerializedName("Model")
    @Expose
    private String model;
    @SerializedName("x_size")
    @Expose
    private String xSize;
    @SerializedName("y_size")
    @Expose
    private String ySize;
    @SerializedName("z_size")
    @Expose
    private String zSize;

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getXSize() {
        return xSize;
    }

    public void setXSize(String xSize) {
        this.xSize = xSize;
    }

    public String getYSize() {
        return ySize;
    }

    public void setYSize(String ySize) {
        this.ySize = ySize;
    }

    public String getZSize() {
        return zSize;
    }

    public void setZSize(String zSize) {
        this.zSize = zSize;
    }
}
