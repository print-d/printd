package com.printdinc.printd.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.databinding.ActivityBedLevelBinding;
import com.printdinc.printd.model.ConnectionState;
import com.printdinc.printd.model.ConnectionStateState;
import com.printdinc.printd.model.PrintHeadCommand;
import com.printdinc.printd.model.SimpleCommand;
import com.printdinc.printd.service.OctoprintService;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by andrewthomas on 3/9/17.
 */

public class BedLevelViewModel implements ViewModel {

    private static final String TAG = "MainViewModel";

    private Context context;
    private Activity activity;
    private Subscription subscription;
    private int current_step;

    // int[x, y, z]
    private int[] printer_dimensions;

    public ObservableField<String> buttonText;

    public ObservableField<String> instructionsText;

    public ObservableInt progressVisibility;

    public ObservableInt image_visibility;


    public BedLevelViewModel(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        progressVisibility = new ObservableInt(View.INVISIBLE);
        image_visibility = new ObservableInt(View.GONE);
        makeSurePrinterIsConnectedForBedLevel();
        buttonText = new ObservableField<String>(context.getString(R.string.start));
        instructionsText = new ObservableField<String>(context.getString(R.string.bed_level_start_instructions));
        printer_dimensions = get_printer_dimensions_from_server();
        current_step = 1;
    }

    public void resumeActivity() {
        progressVisibility.set(View.INVISIBLE);
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
    }

    private int[] get_printer_dimensions_from_server() {
        // Placeholder for now
        return new int[]{200, 200, 180};
    }

    private void doPrintHeadCommand(PrintHeadCommand phc) {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
        subscription = octoprintService.printHeadCommand(phc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error: ", error);
                    }

                    @Override
                    public void onNext(ResponseBody r) {
                        Log.i(TAG, "ResponseBody loaded " + r);
                    }
                });
    }

    private void firstStep() {
        PrintHeadCommand home_command = new PrintHeadCommand("home", new ArrayList<String>(Arrays.asList("x", "y", "z")), 0, 0, 0, false);
        doPrintHeadCommand(home_command);
    }

    private PrintHeadCommand jogCommand(int x, int y, int z) {
        PrintHeadCommand phc = new PrintHeadCommand("jog", new ArrayList<String>(Arrays.asList("x", "y", "z")), x, y, z, true);
        return phc;
    }

    public void onClickNext(View view) {
        if (current_step >= 5) {
            // Finish
            new AlertDialog.Builder(context)
                    .setIcon(0)
                    .setTitle(context.getString(R.string.completebedleveling))
                    .setMessage(context.getString(R.string.areyoufinished))
                    .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Go back here
                            activity.finish();

                        }
                    })
                    .show();
        } else if (current_step == 1) {
            new AlertDialog.Builder(context)
                    .setIcon(0)
                    .setTitle(context.getString(R.string.begin))
                    .setMessage(context.getString(R.string.areyoureadytogetstarted))
                    .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            current_step++;
                            instructionsText.set(context.getString(R.string.bed_level_instructions));
                            image_visibility.set(View.VISIBLE);
                            buttonText.set(context.getString(R.string.nextstep));
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(context)
                    .setIcon(0)
                    .setTitle(context.getString(R.string.proceed))
                    .setMessage(context.getString(R.string.proceedtothenextcorner))
                    .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Go to the next corner
                            moveToNextCorner();
                            current_step++;
                            if (current_step == 5) {
                                buttonText.set(context.getString(R.string.complete));
                            }

                        }
                    })
                    .show();
        }
    }

    private void moveToNextCorner() {
        int x = 0, y = 0;
        switch(current_step) {
            case 1:
                x = printer_dimensions[0] - 10;
                y = 0;
                break;
            case 2:
                //x = 0;
                x = printer_dimensions[0] - 10;
                y = printer_dimensions[1] - 10;
                break;
            case 3:
                //x = 20 - printer_dimensions[0];
                x = 0;
                y = printer_dimensions[1] - 10;
                //y = 0;
                break;
            default: // Something bad happened.
                x = 0;
                y = 0;
                break;
        }
        PrintHeadCommand jog_command = new PrintHeadCommand("jog", new ArrayList<String>(Arrays.asList("x", "y", "z")), x, y, 0, true);
        doPrintHeadCommand(jog_command);
    }

    private void errorPrinting() {
        progressVisibility.set(View.INVISIBLE);
        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle(context.getString(R.string.errorbedleveling))
                .setMessage(context.getString(R.string.errorbedleveling2))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Go back here
                        activity.finish();

                    }
                })
                .show();
    }

    private void makeSurePrinterIsConnectedForBedLevel() {
        progressVisibility.set(View.VISIBLE);
        makeSurePrinterIsConnectedForBedLevel(0);
    }

    private void makeSurePrinterIsConnectedForBedLevel(final int depth) {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
        subscription = octoprintService.getConnectionState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ConnectionState>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error printing", error);
                        errorPrinting();
                    }

                    @Override
                    public void onNext(ConnectionState cs) {

                        ConnectionStateState realCS = cs.getCurrent();

                        if (realCS.getState().equals("Connecting"))
                        {
                            try {
                                makeSurePrinterIsConnectedForBedLevel(depth + 1);
                            }
                            catch(Exception e) {
                                errorPrinting();
                            }
                        }
                        else if (!realCS.getState().equals("Operational")) {

                            if (realCS.getState().startsWith("Error") && depth > 0)
                            {
                                errorPrinting();
                            }
                            else {
                                connectPrinterForBedLevel(depth);
                            }

                        }
                        else
                        {
                            //uploadFile(fileUri);
                            // Do something now that you are connected.
                            progressVisibility.set(View.INVISIBLE);
                        }

                    }
                });
    }

    private void connectPrinterForBedLevel(final int depth) {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
        subscription = octoprintService.connectCommand(new SimpleCommand("connect"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "completed!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error printing", error);
                        errorPrinting();
                    }

                    @Override
                    public void onNext(ResponseBody cs) {
                        try {
                            makeSurePrinterIsConnectedForBedLevel(depth + 1);
                        }
                        catch (Exception e) {
                            makeSurePrinterIsConnectedForBedLevel(depth + 1);
                        }
                    }
                });
    }




}
