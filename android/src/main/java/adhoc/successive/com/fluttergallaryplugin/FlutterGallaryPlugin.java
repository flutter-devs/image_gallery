package adhoc.successive.com.fluttergallaryplugin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;


public class FlutterGallaryPlugin implements MethodCallHandler {
    Activity activity;
    MethodChannel methodChannel;
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    Result result;

    MethodCall mMethodCall;

    HashMap<String, List> allImageInfoList = new HashMap<>();

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "image_gallery");
        channel.setMethodCallHandler(new FlutterGallaryPlugin(registrar.activity(), channel, registrar));
    }

    public FlutterGallaryPlugin(Activity activity, final MethodChannel methodChannel, Registrar registrar) {
        this.activity = activity;
        this.methodChannel = methodChannel;
        this.methodChannel.setMethodCallHandler(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            this.activityLifecycleCallbacks =
                    new Application.ActivityLifecycleCallbacks() {
                        @Override
                        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        }

                        @Override
                        public void onActivityStarted(Activity activity) {
                        }

                        @Override
                        public void onActivityResumed(Activity activity) {

                            if (mMethodCall.method.equals("getAllImages")) {
                                getPermissionResult(result, activity,true);
                            } else if(mMethodCall.method.equals("getAllVideos")){
                                getPermissionResult(result,activity,false);
                            }else
                            {
                                result.notImplemented();
                            }

                        }

                        @Override
                        public void onActivityPaused(Activity activity) {

                        }

                        @Override
                        public void onActivityStopped(Activity activity) {

                        }

                        @Override
                        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                        }

                        @Override
                        public void onActivityDestroyed(Activity activity) {
                        }
                    };
        }

    }


    @Override
    public void onMethodCall(MethodCall call, Result result) {

        mMethodCall = call;

        this.result = result;
        if (call.method.equals("getAllImages")) {
            getPermissionResult(result, activity,true);
        } else if(call.method.equals("getAllVideos")){
            getPermissionResult(result,activity,false);
        }else
        {
            result.notImplemented();
        }
    }


    public void getPermissionResult(final Result result, final Activity activity, final boolean isImage) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(isImage)
                        result.success(getAllImageList(activity));
                        else
                        result.success(getAllVideoList(activity));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("This permission is needed for use this features of the app so please, allow it!");
                        builder.setTitle("We need this permission");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("This permission is needed for use this features of the app so please, allow it!");
                        builder.setTitle("We need this permission");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                token.continuePermissionRequest();

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                token.cancelPermissionRequest();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }).check();

    }


    public HashMap<String, List> getAllImageList(Activity activity) {

        ArrayList<String> allImageList = new ArrayList<>();
        ArrayList<String> displayNameList = new ArrayList<>();
        ArrayList<String> dateAddedList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.TITLE};

        Cursor c = activity.getContentResolver().query(uri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                Log.e("", "getAllImageList: " + c.getString(0));
                Log.e("", "getAllImageList: " + c.getString(1));
                Log.e("", "getAllImageList: " + c.getString(2));
                Log.e("", "getAllImageList: " + c.getString(3));

                titleList.add(c.getString(3));
                displayNameList.add(c.getString(1));
                dateAddedList.add(c.getString(2));
                allImageList.add(c.getString(0));
            }
            c.close();

            allImageInfoList.put("URIList", allImageList);
            allImageInfoList.put("DISPLAY_NAME", displayNameList);
            allImageInfoList.put("DATE_ADDED", dateAddedList);
            allImageInfoList.put("TITLE", titleList);
            allImageInfoList.put("THUMBNAIL", titleList);

        }
        return allImageInfoList;
    }








    public HashMap<String, List> getAllVideoList(Activity activity) {

        ArrayList<String> allVideoList = new ArrayList<>();
        ArrayList<String> displayNameList = new ArrayList<>();
        ArrayList<String> dateAddedList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
        };
        Cursor c = activity.getContentResolver().query(uri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
//                Log.e("", "getAllImageList: " + c.getString(0));
//                Log.e("", "getAllImageList: " + c.getString(1));
//                Log.e("", "getAllImageList: " + c.getString(2));
//                Log.e("", "getAllImageList: " + c.getString(3));
                Log.e("", "getAllImageList: " + c.getString(0));
                Log.e("", "getAllImageList: " + c.getString(3));

                titleList.add(c.getString(3));
                displayNameList.add(c.getString(1));
                dateAddedList.add(c.getString(2));
                allVideoList.add(c.getString(0));
            }
            c.close();

            allImageInfoList.put("URIList", allVideoList);
            allImageInfoList.put("DISPLAY_NAME", displayNameList);
            allImageInfoList.put("DATE_ADDED", dateAddedList);
            allImageInfoList.put("DURATION", titleList);

        }
        return allImageInfoList;
    }
}
