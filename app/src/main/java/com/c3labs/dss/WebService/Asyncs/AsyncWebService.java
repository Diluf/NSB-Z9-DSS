package com.c3labs.dss.WebService.Asyncs;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.FullscreenActivity;
import com.c3labs.dss.Recievers.MyBroadCastReciever;
import com.c3labs.dss.Splash;
import com.c3labs.dss.WebService.Asyncs.Tasks.DoOnPostExecTasks;
import com.c3labs.dss.WebService.CallWeb;

/**
 * Created by c3 on 2/6/2018.
 */

public class AsyncWebService extends AsyncTask<String, String, String> {
    private static final String TAG = "**************";
    Context context;
    int taskId;

    public AsyncWebService(Context context, int taskId) {
        this.context = context;
        this.taskId = taskId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (taskId == MyConstants.LOGIN) {
            Splash splash = (Splash) context;
            splash.statusMessage.setText("Verifying.....");
        } else if (taskId == MyConstants.GET_TIME) {
            if (!MyValues.isResetApp() && context instanceof Splash) {
                Splash splash = (Splash) context;
                splash.statusMessage.setText("Setting Time.....");
            }
        } else if (taskId == MyConstants.GET_NEWS) {
//            Splash splash = (Splash) context;
//            splash.statusMessage.setText("Getting News Lines...");
        } else if (taskId == MyConstants.GET_TEMPLATE_SEQUENCE) {
            if (!MyValues.isResetApp() && context instanceof Splash) {
                Splash splash = (Splash) context;
                splash.statusMessage.setText("Getting Template Sequence...");
            }
        } else if (taskId == MyConstants.GET_SCHEDULES) {
            if (!MyValues.isResetApp() && context instanceof Splash) {
                Splash splash = (Splash) context;
                splash.statusMessage.setText("Getting Schedules...");
            }
        } else if (taskId == MyConstants.GET_REQUIRED_FILES) {
//            FullscreenActivity fullscreenActivity = (FullscreenActivity) context;
//            if (MyBroadCastReciever.isNetworkConnected(context)) {
//
//            }
        }


    }

    @Override
    protected String doInBackground(String... strings) {
        if (MyBroadCastReciever.isNetworkConnected(context)) {
            try {
//                Thread.sleep(1000 * 40);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String response = new CallWeb().callService(strings[0]);
            if (response.equalsIgnoreCase("timeOutEx")) {
//                new AsyncWebService(context, taskId).execute(strings[0]);

            } else {
                return response;
            }
        }
        return "ErrorCalling";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onPostExecute ------------------------------------" + result);

        if (!result.equalsIgnoreCase("ErrorCalling")) {
            if (taskId == MyConstants.LOGIN) {
                new DoOnPostExecTasks().saveUserId(result, context);
            } else if (taskId == MyConstants.GET_TIME) {
                new DoOnPostExecTasks().setSystemTime(result, context);
            } else if (taskId == MyConstants.GET_TEMPLATE_SEQUENCE) {
                new DoOnPostExecTasks().saveTemplateSequence(result, context);
            } else if (taskId == MyConstants.GET_SCHEDULES) {
                new DoOnPostExecTasks().saveSchedules(result, context);
                if (!MyValues.isResetApp() && context instanceof Splash) {
                    Splash splash = (Splash) context;
                    splash.unregReciever();
                    context.startActivity(new Intent(this.context, FullscreenActivity.class));
                    splash.finish();
                } else {
                    ((FullscreenActivity) context).initialize();
                }
            } else if (taskId == MyConstants.GET_REQUIRED_FILES) {
                new DoOnPostExecTasks().checkRequiredFiles(result, context);
            } else if (taskId == MyConstants.GET_NEWS) {
                new DoOnPostExecTasks().saveNewsLines(result, context);
            } else if (taskId == MyConstants.HEART_BEAT) {
                new DoOnPostExecTasks().checkHeartBeatPacket(result, context);
            }
        } else {
            Log.d(TAG, "onPostExecute: No Network - Error Calling");
        }
    }
}
