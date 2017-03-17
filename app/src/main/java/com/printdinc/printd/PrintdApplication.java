package com.printdinc.printd;

import android.app.Application;
import android.content.Context;

import rx.Scheduler;
import rx.schedulers.Schedulers;

import com.printdinc.printd.service.ThingiverseAuthService;
import com.printdinc.printd.service.ThingiverseAuthServiceGenerator;
import com.printdinc.printd.service.ThingiverseServiceGenerator;
import com.printdinc.printd.service.ThingiverseService;

public class PrintdApplication extends Application {

    private ThingiverseService thingiverseService;
    private ThingiverseAuthService thingiverseAuthService;
    private Scheduler defaultSubscribeScheduler;

    public static PrintdApplication get(Context context) {
        return (PrintdApplication) context.getApplicationContext();
    }

    public ThingiverseService getThingiverseService() {
        if (thingiverseService == null) {
            thingiverseService =
                    ThingiverseServiceGenerator.createService(ThingiverseService.class);
        }
        return thingiverseService;
    }

    public ThingiverseAuthService getThingiverseAuthService() {
        if (thingiverseAuthService == null) {
            thingiverseAuthService =
                    ThingiverseAuthServiceGenerator.createService(ThingiverseAuthService.class);
        }
        return thingiverseAuthService;
    }

    //For setting mocks during testing & after getting auth token
    public void setThingiverseService(ThingiverseService thingiverseService) {
        this.thingiverseService = thingiverseService;
    }
    public void setThingiverseAuthService(ThingiverseAuthService thingiverseAuthService) {
        this.thingiverseAuthService = thingiverseAuthService;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    //User to change scheduler from tests
    public void setDefaultSubscribeScheduler(Scheduler scheduler) {
        this.defaultSubscribeScheduler = scheduler;
    }
}
