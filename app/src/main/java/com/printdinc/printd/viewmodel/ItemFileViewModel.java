package com.printdinc.printd.viewmodel;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.printdinc.printd.PrintdApplication;
import com.printdinc.printd.R;
import com.printdinc.printd.model.Position;
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
                .setPositiveButton("Print", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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

    public String getThumbnailUrl() {
        return thingFile.thumbnail;
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setFile(ThingiverseThingFile thingFile) {
        this.thingFile = thingFile;
        notifyChange();
    }

    private void downloadFile(String uri) {
        PrintdApplication application = PrintdApplication.get(context);
        ThingiverseService thingiverseService = application.getThingiverseService();

        Call<ResponseBody> call = thingiverseService.downloadFile(uri);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                    uploadFile(Uri.fromFile(new File(context.getExternalFilesDir(null) + File.separator + context.getString(R.string.stlname))));
                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
            }
        });
    }

    private void printItem(String filename, Position position) {
        try {
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

                        }

                        @Override
                        public void onNext(ResponseBody r) {
                            Log.i(TAG, "ResponseBody loaded " + r);
                        }
                    });
        }
        catch(Exception e)
        {
            Object a = e;
        }

    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
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

                return true;
            } catch (IOException e) {
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
            return false;
        }
    }

    private void uploadFile(Uri fileUri) {
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

                    printItem(context.getString(R.string.stlname), new Position(100l,100l));
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
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

