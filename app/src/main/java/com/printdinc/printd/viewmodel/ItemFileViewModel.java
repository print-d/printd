package com.printdinc.printd.viewmodel;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.ConnectionState;
import com.printdinc.printd.model.ConnectionStateState;
import com.printdinc.printd.model.Position;
import com.printdinc.printd.model.SimpleCommand;
import com.printdinc.printd.model.SliceCommand;
import com.printdinc.printd.model.ThingiverseThingFile;
import com.printdinc.printd.service.OctoprintService;
import com.printdinc.printd.service.ThingiverseService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by andrewthomas on 3/21/17.
 */

public class ItemFileViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = "ItemFileViewModel";

    private ThingiverseThingFile thingFile;
    private Context context;
    private Subscription subscription;

    private ProgressDialog progressDialog;
    private Handler progressHandler;

    public ItemFileViewModel(Context context, ThingiverseThingFile thingFile) {
        this.thingFile = thingFile;
        this.context = context;
    }

    public String getName() {
        return this.thingFile.name;
    }

    public void onItemClick(View view) {

        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle("Print file")
                .setMessage("Are you sure you want to print this file?\n" + this.thingFile.name)
                .setPositiveButton(context.getString(R.string.print), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressHandler = new Handler();
                        progressDialog = new ProgressDialog(context);

                        progressDialog.setTitle("Sending to Printer...");
                        progressDialog.setMessage("Sending to printer...");
                        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
                        progressDialog.setProgress(0);
                        progressDialog.setMax(5);
                        progressDialog.show();

                        downloadFile(thingFile.download_url);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    private void errorPrinting() {
        progressDialog.dismiss();
        new AlertDialog.Builder(context)
                .setIcon(0)
                .setTitle("Error Printing")
                .setMessage("There was an error printing.\nMake sure your printer is turned on.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                    })
                    .show();
    }

    private void makeSurePrinterIsConnectedForUpload(final Uri fileUri) {
        makeSurePrinterIsConnectedForUpload(fileUri, 0);
    }

    private void makeSurePrinterIsConnectedForUpload(final Uri fileUri, final int depth) {
        progressDialog.setMessage("Connecting to printer...");
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
                                Thread.sleep(5000l);
                                makeSurePrinterIsConnectedForUpload(fileUri, depth + 1);
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
                                connectPrinterForUpload(fileUri, depth);
                            }

                        }
                        else
                        {
                            progressDialog.incrementProgressBy(1);

                            uploadFile(fileUri);
                        }

                    }
                });
    }

    private void connectPrinterForUpload(final Uri fileUri, final int depth) {
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
                            Thread.sleep(5000l);
                            makeSurePrinterIsConnectedForUpload(fileUri, depth + 1);
                        }
                        catch (Exception e) {
                            makeSurePrinterIsConnectedForUpload(fileUri, depth + 1);
                        }
                    }
                });
    }

    public String getThumbnailUrl() {
        return thingFile.thumbnail;
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setFile(ThingiverseThingFile thingFile) {
        this.thingFile = thingFile;
        notifyChange();
    }

    private void downloadFile(String uri) {
        progressDialog.setMessage("Downloading file...");

        PrintdApplication application = PrintdApplication.get(context);
        ThingiverseService thingiverseService = application.getThingiverseService();

        Call<ResponseBody> call = thingiverseService.downloadFile(uri);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    progressDialog.incrementProgressBy(1);

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                    makeSurePrinterIsConnectedForUpload(Uri.fromFile(new File(context.getExternalFilesDir(null) + File.separator + context.getString(R.string.stlname))));
                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
                errorPrinting();
            }
        });
    }

    private void printItem(String filename, Position position) {
        progressDialog.setMessage("Issuing print command...");

        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService octoprintService = application.getOctoprintService();
        subscription = octoprintService.issuePrintCommand(context.getString(R.string.location), filename, new SliceCommand(context.getString(R.string.sliceCommand), position, true))
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
                    public void onNext(ResponseBody r) {

                        progressDialog.incrementProgressBy(1);

                        if (progressDialog.getProgress() == progressDialog.getMax()) {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setIcon(0)
                                    .setTitle("Print file")
                                    .setMessage("The file was successfully uploaded!\nIt will begin printing once it is sliced.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                        } else {
                            errorPrinting();
                        }

                        Log.i(TAG, "ResponseBody loaded " + r);
                    }
                });


    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        progressDialog.setMessage("Writing file to storage...");
        try {
            File futureStudioIconFile = new File(context.getExternalFilesDir(null) + File.separator + context.getString(R.string.stlname));

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                progressDialog.incrementProgressBy(1);

                return true;
            } catch (IOException e) {
                errorPrinting();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            errorPrinting();
            return false;
        }
    }

    private void uploadFile(Uri fileUri) {
        progressDialog.setMessage("Uploading to OctoPrint for printing...");

        // create upload service client
        PrintdApplication application = PrintdApplication.get(context);
        OctoprintService service = application.getOctoprintService();

        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(context, fileUri);

        try {
            ContentResolver cr = context.getContentResolver();
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getMimeType(fileUri.toString())),
                            file
                    );


            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);


            // finally, execute the request
            Call<ResponseBody> call = service.uploadFile(context.getString(R.string.location), body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    Log.v("Upload", "success");

                    progressDialog.incrementProgressBy(1);

                    //TODO use positions from Heroku Server
                    printItem(context.getString(R.string.stlname), new Position(100l,100l));
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());

                    errorPrinting();
                }
            });
        }
        catch (Exception e) {
            Object a = e;
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
        context = null;
    }
}

