package com.printdinc.printd.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("op_apikey")
    @Expose
    private String op_apikey;
    @SerializedName("make")
    @Expose
    private String make;
    @SerializedName("model")
    @Expose
    private String model;

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getOP_APIKey() {return op_apikey;}
    public String getMake() {return make;}
    public String getModel() {return model;}

    public User(String username, String password, String op_apikey, String make, String model) {
        this.username = username;
        this.password = password;
        this.op_apikey = op_apikey;
        this.make = make;
        this.model = model;
    }
}
