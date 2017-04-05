package com.printdinc.printd;

import android.app.Application;
import android.content.Context;

import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.service.OctoprintServiceGenerator;
import com.printdinc.printd.service.ThingiverseAuthService;
import com.printdinc.printd.service.ThingiverseAuthServiceGenerator;
import com.printdinc.printd.service.ThingiverseService;
import com.printdinc.printd.service.ThingiverseServiceGenerator;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class PrintdApplication extends Application {

    private ThingiverseService thingiverseService;
    private ThingiverseAuthService thingiverseAuthService;
    private OctoprintService octoprintService;
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

    public OctoprintService getOctoprintService() {
        if (octoprintService == null) {
            octoprintService =
                    OctoprintServiceGenerator.createService(OctoprintService.class);
        }
        return octoprintService;
    }

    //For setting mocks during testing & after getting auth token
    public void setThingiverseService(ThingiverseService thingiverseService) {
        this.thingiverseService = thingiverseService;
    }
    public void setThingiverseAuthService(ThingiverseAuthService thingiverseAuthService) {
        this.thingiverseAuthService = thingiverseAuthService;
    }
    public void setOctoprintService(OctoprintService octoprintService) {
        this.octoprintService = octoprintService;
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
