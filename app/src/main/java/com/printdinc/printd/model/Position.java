package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("x")
    @Expose
    private long x;
    @SerializedName("y")
    @Expose
    private long y;

    public long getX() {
        return x;
    }

    public long getY() { return y; }

    public Position(long x, long y) {
        this.x = x;
        this.y = y;
    }
}