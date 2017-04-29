package com.printdinc.printd;

import android.app.Application;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.printdinc.printd.model.NewNSDInfo;
import com.printdinc.printd.service.HerokuService;
import com.printdinc.printd.service.HerokuServiceGenerator;
import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.service.ThingiverseService;
import com.printdinc.printd.service.ThingiverseServiceGenerator;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class PrintdApplication extends Application {

    private ThingiverseService thingiverseService;
//    private ThingiverseAuthService thingiverseAuthService;
    private HerokuService herokuService;
    private OctoprintService octoprintService;
    private Scheduler defaultSubscribeScheduler;

    public boolean discoveringServices;

    private ArrayAdapter<NewNSDInfo> services;
    public int mSelectedItem = -1;

    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdManager mNsdManager;

    public static final String SERVICE_TYPE = "_http._tcp.";

    public static PrintdApplication get(Context context) {
        return (PrintdApplication) context.getApplicationContext();
    }

    public NsdManager getmNsdManager() {
        if (mNsdManager == null) {
            mNsdManager = (NsdManager) this.getSystemService(Context.NSD_SERVICE);

            discoveringServices = false;

            initializeNsd();
            if (!(discoveringServices)) {
                discoverServices();
                discoveringServices = true;
            }
        }

        return mNsdManager;
    }

    private void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.

                // The name of the service tells the user what they'd be
                // connecting to. It could be "Bob's Chat App".

//                    new AddNsdServiceInfoTask().execute(new NewNSDInfo(service));
//                        services.add(service);

                mNsdManager.resolveService(service, mResolveListener);


            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.

//                services.remove(service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.i("PrintApplication", "Resolved service " + serviceInfo.getHost().getHostAddress() + " " + serviceInfo.getHost().toString());

                //if (serviceInfo.getServiceType())
                // Have to use an async task to avoid setting something on a thread that doesn't own it
                new PrintdApplication.AddNsdServiceInfoTask().execute(new NewNSDInfo(serviceInfo));
            }
        };
    }

    public void initializeNsd() {
        initializeDiscoveryListener();
        initializeResolveListener();

    }

    public void discoverServices() {
        try {

            this.getServices();

            this.getmNsdManager().discoverServices(
                    SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            discoveringServices = true;
        }
        catch (Exception error) {
            System.out.println(error);
        }

    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        discoveringServices = false;
    }


    // A subclass is used so that the services list is accessable
    class AddNsdServiceInfoTask extends AsyncTask<NewNSDInfo, NewNSDInfo, Void> {
        @Override
        protected Void doInBackground(NewNSDInfo... params) {
            for (NewNSDInfo item : params) {
                publishProgress(item);
            }

            return null;
        }

        //@SuppressWarnings("unchecked")
        protected void onProgressUpdate(NewNSDInfo... item) {
            for (int i = 0; i < services.getCount(); i++) {
                if (services.getItem(i).toString() != null &&
                        services.getItem(i).toString().equals(item[0].toString()) )
                {
                    return;
                }
            }
            services.add(item[0]);
        }

    }

    public ArrayAdapter<NewNSDInfo> getServices() {
        if (services == null) {
            services = new ArrayAdapter<NewNSDInfo>(this, android.R.layout.select_dialog_singlechoice);
        }

        return services;
    }

    public HerokuService getHerokuService() {
        if (herokuService == null) {
            herokuService =
                    HerokuServiceGenerator.createService(HerokuService.class);
        }
        return herokuService;
    }

    public ThingiverseService getThingiverseService() {
        if (thingiverseService == null) {
            thingiverseService =
                    ThingiverseServiceGenerator.createService(ThingiverseService.class);
        }
        return thingiverseService;
    }

//    public ThingiverseAuthService getThingiverseAuthService() {
//        if (thingiverseAuthService == null) {
//            thingiverseAuthService =
//                    ThingiverseAuthServiceGenerator.createService(ThingiverseAuthService.class);
//        }
//        return thingiverseAuthService;
//    }

    public OctoprintService getOctoprintService() {
//        if (octoprintService == null) {
//            octoprintService =
//                    OctoprintServiceGenerator.createService(OctoprintService.class);
//        }
        return octoprintService;
    }

    //For setting mocks during testing & after getting auth token
    public void setThingiverseService(ThingiverseService thingiverseService) {
        this.thingiverseService = thingiverseService;
    }
    public void setHerokuService(HerokuService herokuService) {
        this.herokuService = herokuService;
    }
//    public void setThingiverseAuthService(ThingiverseAuthService thingiverseAuthService) {
//        this.thingiverseAuthService = thingiverseAuthService;
//    }
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
