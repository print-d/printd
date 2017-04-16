package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pdixon on 4/16/2017.
 */

public class PrinterData {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("printers")
    @Expose
    private List<Printer> printers = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public void setPrinters(List<Printer> printers) {
        this.printers = printers;
    }
}
