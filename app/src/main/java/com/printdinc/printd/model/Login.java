package com.printdinc.printd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pdixon on 4/11/2017.
 */

public class Login {
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Password")
    @Expose
    private String password;

    public String getUsername() {return username;}
    public String getPassword() {return password;}

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
