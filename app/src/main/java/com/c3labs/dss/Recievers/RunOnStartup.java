package com.c3labs.dss.Recievers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.widget.Toast;

import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.Splash;
import com.c3labs.dss.WebService.Asyncs.AsyncWebService;
import com.c3labs.dss.WebService.Refferences;

import java.io.File;

/**
 * Created by Abdallah Elsyd Shehata  on 31/08/2016.
 * copy reserved abdallah.elsyd.sh@gmail.com
 */
public class RunOnStartup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, intent.getAction() + "------Myyyy----", Toast.LENGTH_LONG).show();
        Log.d("[[[[[[[[[[[[[[", "instance: " + (context instanceof Splash));
        try {
//            Thread.sleep(2000 * 20);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) ) {
                    Intent i = new Intent(context, Splash.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                    if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                        Log.d("[[[[[[[[[[[---", "onReceive: Replaced");
                        Log.d("[[[[[[[[[[[---", MyValues.getNewApkName());

                        File file = new File(Methods.createOrGetDirectory() + MyValues.getNewApkName());
                        if (file.exists()) {
                            file.delete();
                        }
                        MyValues.setUpdateBeat(true);

                    }
//                    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//                        MyValues.setBootCompleted(true);
//                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
