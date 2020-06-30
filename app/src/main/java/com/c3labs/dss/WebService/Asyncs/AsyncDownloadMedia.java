package com.c3labs.dss.WebService.Asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.FullscreenActivity;
import com.c3labs.dss.R;
import com.c3labs.dss.WebService.Refferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by c3 on 2/8/2018.
 */

public class AsyncDownloadMedia extends AsyncTask<ArrayList<String>, Integer, ArrayList<String>> {
    private static final String TAG = "**********";
    Context context;
    FullscreenActivity fullscreenActivity;
    ArrayList<String> unAvailables;

    public AsyncDownloadMedia(Context context, ArrayList<String> unAvailables) {
        this.context = context;
        this.unAvailables = unAvailables;
        fullscreenActivity = (FullscreenActivity) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!unAvailables.isEmpty()) {
            fullscreenActivity.statusIcon.setImageResource(R.drawable.download);
//            fullscreenActivity.statusIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.zoom_in_out_all_splash_logo));
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d("###################", "onProgressUpdate: " + values[2]);


        String text = "<b><font color='#ffb200'>" + (values[0] + 1) + "</font></b>/" + values[1];
        fullscreenActivity.progCount.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);


//        .setText("(" + values[0] + "/" + values[1] + ")");
//        fullscreenActivity.progPercentage.setText(values[2] + "%");
        fullscreenActivity.progressBar.setProgress(values[2]);
    }

    @Override
    protected ArrayList<String> doInBackground(ArrayList<String>[] arrayLists) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        URL url = null;
//        ArrayList<String> doneArrayList = new ArrayList<>();
        String path = "";
        for (int i = 0; i < unAvailables.size(); i++) {
            try {
//                url = new URL("http://203.143.20.94/nsban/upload_assets/upload_media/" + unAvailables.get(i));
                url = new URL(Refferences.getMediaPath.method + unAvailables.get(i));

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "doInBackground: Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                path = Methods.createOrGetDirectory().getAbsolutePath() + File.separator + "_" + unAvailables.get(i);
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }

                input = connection.getInputStream();
                output = new FileOutputStream(path);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    output.write(data, 0, count);
                    if (fileLength > 0) {// only if total length is known
                        int prog = (int) (total * 100 / fileLength);
                        publishProgress(i, unAvailables.size(), prog);
                        if (prog == 100) {
                            file.renameTo(new File(Methods.createOrGetDirectory().getAbsolutePath() + File.separator + unAvailables.get(i)));
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: " + e.toString());
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
        }
        Methods methods = new Methods();
        Log.d(TAG, "doInBackground: AsyncURL");

        ArrayList<File> files = new ArrayList(Arrays.asList(methods.createOrGetDirectory().listFiles()));
        files.remove(new File(methods.createOrGetDirectory() + File.separator + "loginfile.txt"));
        new AsyncURLConnection(context).execute(files);

//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        File Removing
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
//        ------------------------------------------------------------------------------------
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean isPossibleToRemove;
            for (File file :
                    files) {
                isPossibleToRemove = true;
                for (String fileName :
                        arrayLists[0]) {
                    if (fileName.trim().equalsIgnoreCase(file.getName())) {
                        isPossibleToRemove = false;
                        arrayLists[0].remove(fileName);
                        break;
                    }
                }

                if (isPossibleToRemove) {
                    Date modified = simpleDateFormat.parse(simpleDateFormat.format(file.lastModified()));
                    Date now = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
                    long diff = now.getTime() - modified.getTime();
                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) >= 30) {
                        file.delete();
                    }

                    Log.d(TAG, "doInBackground----filedelete: " + file.getName() + "    " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                    Log.d(TAG, "doInBackground----filedelete: " + modified);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return unAvailables;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        fullscreenActivity.progCount.setText("");
        fullscreenActivity.progressBar.setProgress(0);
        fullscreenActivity.statusIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
        fullscreenActivity.statusIcon.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fullscreenActivity.statusIcon.setImageResource(new Methods().getNetworkStatusIcon(context));
                fullscreenActivity.statusIcon.getAnimation().reset();
//                Toast.makeText(context, fullscreenActivity.statusIcon.getScaleX() + "", Toast.LENGTH_SHORT).show();
                Log.d("$$$$$$$$$$$$$---", "onAnimationEnd: ");
                fullscreenActivity.statusIcon.setAlpha(1f);
//                fullscreenActivity.statusIcon.setScaleX(1.2f);
//                fullscreenActivity.statusIcon.setScaleY(1.2f);
                fullscreenActivity.statusIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (MyValues.isResetApp()) {
            new AsyncWebService(context, MyConstants.GET_TIME).execute(Refferences.GetTime.methodName + Methods.getNodeId(context));
        }


        for (String fileName :
                strings) {
            Log.d(TAG, "onPostExecute: " + fileName);
        }

//        Remove Unused Media Files

    }

}
