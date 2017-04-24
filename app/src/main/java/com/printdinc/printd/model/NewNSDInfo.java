package com.printdinc.printd.model;

import android.net.nsd.NsdServiceInfo;

/**
 * Created by andrewthomas on 4/20/17.
 */

public class NewNSDInfo {
    private NsdServiceInfo info;
    public NewNSDInfo(NsdServiceInfo info) {
        this.info = info;
    }

    public String toString() {
        return info//.toString();
                .getServiceName();
    }

    public String getBaseUrl() {
        return info.getHost().getHostAddress();
    }
}
