package com.c3labs.dss.WebService.Asyncs.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
//import android.widget.Toast;

import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.FullscreenActivity;
import com.c3labs.dss.R;
import com.c3labs.dss.Splash;
import com.c3labs.dss.WebService.Asyncs.AsyncDownAndUpdateAPK;
import com.c3labs.dss.WebService.Asyncs.AsyncDownloadMedia;
import com.c3labs.dss.WebService.Asyncs.AsyncWebService;
import com.c3labs.dss.WebService.Refferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by c3 on 2/7/2018.
 */

public class DoOnPostExecTasks {

    private static final String TAG = "DoOnPostExec----------";
    Methods methods = new Methods();

    public void saveUserId(String result, final Context context) {
        try {
            final Splash splash = (Splash) context;
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.get("BranchName").toString().equalsIgnoreCase("-1")) {
                splash.signInAgain.setVisibility(View.VISIBLE);
                splash.errorlayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                splash.doIndeterminate(false);
                splash.statusMessage.setText("");
            } else {
                SharedPreferences sharedPref = Methods.getSharedPref(context);
                String nodeId = jsonObject.getString("Status");
                sharedPref.edit().putString("nodeId", nodeId).commit();
                sharedPref.edit().putString("branchName", jsonObject.getString("BranchName")).commit();
//                MyValues.setNodeId(nodeId);
//                MyValues.setBranchName(jsonObject.getString("BranchName"));
//                Log.d(TAG, "saveUserId: " + );


                if (splash.errorlayout.getAnimation() != null) {
                    splash.errorlayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                    splash.errorlayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            splash.errorlayout.setVisibility(View.INVISIBLE);
                            splash.signInAgain.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                new AsyncWebService(context, MyConstants.GET_TIME).execute(Refferences.GetTime.methodName + nodeId);
                new AsyncWebService(context, MyConstants.UPDATE_STATUS_LOGGED).execute(Refferences.UpdateStatus.methodName + nodeId + "/L");
                splash.netStatusHandler.removeCallbacks(splash.netStatusRunnable);
                splash.handler03Min.removeCallbacks(splash.runnable03Min);
                if (MyValues.isUpdateBeat()) {
                    try {
                        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        new AsyncWebService(context, MyConstants.UPDATE_STATUS_UPDATED).execute(Refferences.UpdateStatus.methodName + nodeId + "/U-" + pInfo.versionName);
                        MyValues.setUpdateBeat(false);
//                        Toast.makeText(splash, "DoneUpdatedBeat", Toast.LENGTH_SHORT).show();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                splash.crashApp = true;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSystemTime(String result, Context context) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            methods.setDateToSystem(jsonObject.getString("DateTime"));
            saveStringSharedPref("DownloadTime", jsonObject.getString("DownloadTime"), context);
            saveStringSharedPref("DownloadStop", jsonObject.getString("DownloadStop"), context);
            saveStringSharedPref("ExitPassword", jsonObject.getString("Version"), context);

            new AsyncWebService(context, MyConstants.GET_TEMPLATE_SEQUENCE).execute(Refferences.GetTemplateSequence.methodName + Methods.getNodeId(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkRequiredFiles(String result, Context context) {
        try {
//            Toast.makeText(context, MyValues.isResetApp() + " 99", Toast.LENGTH_SHORT).show();
            ArrayList<String> reqFiles = new ArrayList<>();
            ArrayList<String> unAvailables = new ArrayList<>();
            ArrayList<File> files = new ArrayList(Arrays.asList(methods.createOrGetDirectory().listFiles()));
            boolean isAvailable;

            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                isAvailable = false;
                reqFiles.add(jsonArray.getJSONObject(i).getString("fileName").trim());
                for (File file : files) {
                    if (jsonArray.getJSONObject(i).getString("fileName").trim().equalsIgnoreCase(file.getName())) {
                        isAvailable = true;
                        files.remove(file);
                        break;
                    }
                }
                if (!isAvailable) {
                    unAvailables.add(jsonArray.getJSONObject(i).getString("fileName").trim());
                }
            }
            Log.d(TAG, "checkRequiredFiles: UnAvAiLaBLeS----" + unAvailables.size());
//            if (!unAvailables.isEmpty()) {
            new AsyncDownloadMedia(context, unAvailables).execute(reqFiles);
//            } else if (MyValues.isResetApp()) {
//                new AsyncWebService(context, MyConstants.GET_TEMPLATE_SEQUENCE).execute(Refferences.GetTemplateSequence.methodName + Methods.getNodeId(context));
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveNewsLines(String result, Context context) {
//        saveStringSharedPref("news", result, context);
        setNewsLines(result, context);
    }

    public void saveTemplateSequence(String result, Context context) {
        saveStringSharedPref("sequence", result, context);
        String url = Refferences.GetSchedules.methodName + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + Methods.getNodeId(context);
        new AsyncWebService(context, MyConstants.GET_SCHEDULES).execute(url);

    }

    public void saveSchedules(String result, Context context) {
        saveStringSharedPref("schedules", result, context);
    }

    private void setNewsLines(final String result, final Context context) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        try {
            FullscreenActivity fullscreenActivity = (FullscreenActivity) context;
            JSONArray jsonArray = new JSONArray(result);
            String newsString = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                newsString += jsonArray.getJSONObject(i).getString("Body") + " | ";
            }

            float screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            fullscreenActivity.news.setText("");
            fullscreenActivity.news.initScrollTextView(screenWidth, newsString);
            fullscreenActivity.news.setCycle(true);
            fullscreenActivity.news.starScroll();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//            }
//        }).start();
    }

    private void saveStringSharedPref(String name, String value, Context context) {
        SharedPreferences sharedPref = Methods.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public void checkHeartBeatPacket(String result, Context context) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.length() != 0) {
                if (jsonObject.getString("ApplicationRestart").trim().equalsIgnoreCase("1")) {
//Update APK
                    new AsyncDownAndUpdateAPK().execute();
//                    Toast.makeText(context, "============Start APK DOWNLOADING=============", Toast.LENGTH_SHORT).show();

                } else if (jsonObject.getString("DeviceRestart").trim().equalsIgnoreCase("1")) {
//                    Reboot device
                    methods.restartDevice();
                } else {
                    FullscreenActivity fullscreenActivity = (FullscreenActivity) context;
                    if (jsonObject.getString("BackgroundDownloading").trim().equalsIgnoreCase("1")) {
                        MyValues.setResetApp(true);
                        fullscreenActivity.beginDownload();
                    }
                    if (jsonObject.getString("Currency").trim().equalsIgnoreCase("1")) {
                        fullscreenActivity.loadWebView();
                    }
                    if (jsonObject.getString("News").trim().equalsIgnoreCase("1")) {
                        fullscreenActivity.setNewsLines();
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
