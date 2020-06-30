package com.c3labs.dss;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.c3labs.dss.Clz.AutoScrollTextView;
import com.c3labs.dss.Clz.MyExceptionHandler;
import com.c3labs.dss.Clz.ZidooHdmiDisPlay_;
import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.Recievers.MyBroadCastReciever;
import com.c3labs.dss.WebService.Asyncs.AsyncWebService;
import com.c3labs.dss.WebService.Refferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private static final String TAG = "+++++++++++++++++++++++";
    LayoutInflater inflater;
//    LayoutControls layoutControls;

    Handler handlerTemplateSequence;
    Runnable runnableTemplateSequence;
    Handler handlerSchedules;
    Runnable runnableSchedules;
    Handler handlerCatchFullPlayed;
    Runnable runnableCatchFullPlayed;
    Handler handlerSingleSecond;
    Runnable runnableSingleSecond;
    Handler handlerChangeBoolean;
    Runnable runnableChangeBoolean;

    //    SimpleDateFormat simpleDateFormatTime;
    private JSONArray templateSeqJsonArray, normalSchedulesJsonArray, adFullScheduleJsonArray;
    private JSONObject jsonObjectInsideRunnableSchedules;
    private ArrayList<Date[]> adHocTimeArrayList;
    private int adHocStatus;

    private int selectedLayout, selectedSchedule, selectedScheduleNormal;
    private int currentSecond = 0;

    //    private int networkSeconds = 0;
    private boolean changeAdFull, isDownloadStarted;
    private final String[] imageFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
    private SimpleDateFormat simpleDateFormatFull, simpleDateFormatTime;
    private Drawable.ConstantState downloadConstantStateIcon;
    private ZidooHdmiDisPlay_ mRealtekeHdmi = null;
    private ViewGroup hdmiInRelative;
    private String[] currentLayoutDetails;
    private File file;
    private Date[] downloadStartTime;
    private Date date;

    //    Components
    public TextView progCount;
    public ImageView statusIcon;
    public ProgressBar progressBar;
    private RelativeLayout dynamic, currencyMediaWrapper;
    private TextView branchName, dateTime;
    public AutoScrollTextView news;
    private View adFullView, currencyView;
    private VideoView videoViewNormal, videoViewAdFull;
    private ImageView imageViewNormal, imageViewAdFull;
    private Bitmap bitmap;

//    MediaPlayer mpNormal, mpAdFull;

    @Override
    public void onBackPressed() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.dialog_signin, null);
        ImageView asterikL = v.findViewById(R.id.imgV_asterikLeftActivitySplash);
        ImageView asterik = v.findViewById(R.id.imgV_asterikActivitySplash);
        ImageView asterikR = v.findViewById(R.id.imgV_asterikRightActivitySplash);
        final EditText userName = v.findViewById(R.id.et_dialog_signin_UserName);
        final EditText password = v.findViewById(R.id.et_dialog_signin_Password);
//        Button exit = v.findViewById(R.id.btn_dialog_signin_Exit);
        Button signIn = v.findViewById(R.id.btn_dialog_signin_SignIn);

        asterik.startAnimation(AnimationUtils.loadAnimation(this, R.anim.load_asterik));
        asterikL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        asterikR.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        signIn.setText("OK");
        userName.setVisibility(View.GONE);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(v).create();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().trim().equalsIgnoreCase(Methods.getSharedPref(FullscreenActivity.this).getString("ExitPassword", Math.random() + ""))) {
                    handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
                    handlerSchedules.removeCallbacks(runnableSchedules);
                    handlerSingleSecond.removeCallbacks(runnableSingleSecond);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    alertDialog.dismiss();
                }
            }
        });


        alertDialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
        initialize();


//        initializeHandlers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormatFull = new SimpleDateFormat("dd-MMM-yyyy   hh:mm a");

        dynamic = findViewById(R.id.rl_DynamicLayoutActivityFullScreen);
        branchName = findViewById(R.id.tv_branchNameActivityFullScreen);
        dateTime = findViewById(R.id.tv_DateTimeActivityFullScreen);
        news = findViewById(R.id.tv_newsActivityFullScreenNews);
        statusIcon = findViewById(R.id.imgV_NetworkStatusActivityFullScreen);
        progressBar = findViewById(R.id.prog_DownloadedActivitySplash);
        progCount = findViewById(R.id.tv_progressCountActivityFullScreen);
        news.setSelected(true);

        statusIcon.setImageResource(new Methods().getNetworkStatusIcon(this));
        downloadConstantStateIcon = FullscreenActivity.this.getResources().getDrawable(getResources().getIdentifier("@drawable/download",
                null, FullscreenActivity.this.getPackageName()), FullscreenActivity.this.getTheme()).getConstantState();


        findViewById(R.id.btn_TestC3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyExceptionHandler.restartApp(FullscreenActivity.this, Splash.class, new Exception());
            }
        });

        findViewById(R.id.btn_TestC3).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new Methods().restartDevice();

                return false;
            }
        });
//
        findViewById(R.id.btn_test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    Toast.makeText(FullscreenActivity.this, pInfo.versionName, Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, FullscreenActivity.class));


    }

    private void hideAllLayouts() {
        hdmiInRelative.setVisibility(View.GONE);
        currencyMediaWrapper.setVisibility(View.INVISIBLE);
        videoViewNormal.setVisibility(View.GONE);
        mRealtekeHdmi.setAudio(false);

    }


//    -------------------------
//    -------------------------
//    -------------------------
//    -------------------------
//    -------------------------

    private void initializeHandlers() {
        handlerChangeBoolean = new Handler();
        runnableChangeBoolean = new Runnable() {
            @Override
            public void run() {
                changeAdFull = true;
                handlerChangeBoolean.removeCallbacks(this);
            }
        };

        handlerTemplateSequence = new Handler();
        runnableTemplateSequence = new Runnable() {
            @Override
            public void run() {
                methodTemplateSeq();

            }
        };

        handlerTemplateSequence.postDelayed(runnableTemplateSequence, 1000);

        handlerSchedules = new Handler();
        runnableSchedules = new Runnable() {
            @Override
            public void run() {
                methodSchedules();
            }
        };
        handlerSchedules.postDelayed(runnableSchedules, 1500);

        handlerSingleSecond = new Handler();
        runnableSingleSecond = new Runnable() {
            @Override
            public void run() {
                methodSingleSec();
            }
        };
        handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);

        handlerCatchFullPlayed = new Handler();
        runnableCatchFullPlayed = new Runnable() {
            @Override
            public void run() {
                ++selectedScheduleNormal;
                Log.d(TAG, "Current Sche: " + selectedScheduleNormal);
            }
        };

    }


    private void setTimePlusCheckDownloadingTimePlusAdHoc() {
        try {
            dateTime.setText(simpleDateFormatFull.format(date = new Date()));
            checkDownloadTime(date = simpleDateFormatTime.parse(simpleDateFormatTime.format(date)));


            for (Date adHDate[] :
                    adHocTimeArrayList) {
//                Log.d(TAG, "setTimePlusCheckDownloadingTimePlusAdHoc: b4 adHDate" + adHDate[0] + " - " + adHDate[1] + "srry");
//                Log.d(TAG, "setTimePlusCheckDownloadingTimePlusAdHoc: b4 " + date.after(adHDate[0]) + " " + date.before(adHDate[1]));

                if (date.equals(adHDate[0]) ||
                        (date.after(adHDate[0]) && date.before(adHDate[1]))) {
//                    Log.d(TAG, "setTimePlusCheckDownloadingTimePlusAdHoc: Insode" + adHocStatus);
                    if (adHocStatus == MyConstants.ADH_NOT_LOADED) {
                        String url = Refferences.SchedulePlayStatus.methodName + Methods.getNodeId(FullscreenActivity.this) + File.separator +
                                configAdFullSchedules(getDateFromString(simpleDateFormatTime.format(date)));
                        Log.d(TAG, "startTime: " + url);
                        new AsyncWebService(FullscreenActivity.this, MyConstants.SCHEDULE_PLAY_STATUS).execute(url);


                        handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
                        handlerChangeBoolean.removeCallbacks(runnableChangeBoolean);
                        selectedSchedule = -1;
                        changeAdFull = false;

                        //                currentLayoutDetails = loadLayout();
                        setView("AdFullV");
                        loadLayoutSchedules();
                        currentLayoutDetails = new String[]{"AdFullV", ""};
                        //                handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
                        handlerSchedules.postDelayed(runnableSchedules, 1);
                        adHocStatus = MyConstants.ADH_PLAYING;
                    }
                    break;
                    //                return;
                    //                changeAdFull = false;
//                    =======================================AdHoc End Function
                } else if (date.equals(adHDate[2]) ||
                        (date.after(adHDate[2]) && date.before(adHDate[3]))) {
                    if (adHocStatus == MyConstants.ADH_PLAYING) {
                        MyValues.setResetApp(true);
                        adHocStatus = MyConstants.ADH_STOPPED;
                        initialize();
                        //                return;
                        //                changeAdFull = false;
                    }
                    break;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void checkDownloadTime(Date date) {
        Log.d(TAG, "checkDownloadTime: " + date.before(downloadStartTime[0]) + " end = " + date.after(downloadStartTime[1]));
        if (date.equals(downloadStartTime[0]) || (date.after(downloadStartTime[0]) && date.before(downloadStartTime[1]))) {
            if (!isDownloadStarted) {
                MyValues.setResetApp(true);
                beginDownload();
                isDownloadStarted = true;
            }

        }
    }


    private void loadLayoutSchedules() {
        videoViewNormal.stopPlayback();
        videoViewNormal.seekTo(0);
        videoViewNormal.setVisibility(View.GONE);
//        if (mpNormal != null) {
//            mpNormal.release();
//        }
        if (currentLayoutDetails != null) {
            Log.d(TAG, "curreny000000000000--" + currentLayoutDetails[0]);
            if (currentLayoutDetails[0].equalsIgnoreCase("CurrencyHDMIV")) {
                hdmiInRelative.setVisibility(View.VISIBLE);
                currencyMediaWrapper.setVisibility(View.INVISIBLE);
                videoViewNormal.setVisibility(View.GONE);
                mRealtekeHdmi.setAudio(true);

            } else if (currentLayoutDetails[0].equalsIgnoreCase("CurrencySplitV")) {
                hdmiInRelative.setVisibility(View.GONE);
                currencyMediaWrapper.setVisibility(View.VISIBLE);
                videoViewNormal.setVisibility(View.VISIBLE);
                mRealtekeHdmi.setAudio(false);
            }
        }
    }

    private void setHeartBeatPacket() {
        if (currentSecond >= 120) {
            currentSecond = 0;
            new AsyncWebService(this, MyConstants.HEART_BEAT).execute(Refferences.UpdateNodeStatus.methodName + Methods.getNodeId(FullscreenActivity.this));
            System.gc();
        }
    }


    private void setNetStatusImage() {
        if (statusIcon.getDrawable().getConstantState() != downloadConstantStateIcon) {
            if (MyBroadCastReciever.isNetworkConnected(FullscreenActivity.this)) {
                statusIcon.setImageResource(R.drawable.online);
            } else {
                statusIcon.setImageResource(R.drawable.offline);
            }
        }

    }

    private boolean imageVideoConfigsWithIsImage(File file) {
        for (String extension :
                imageFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String[] loadLayout() {
        try {
            String[] viewMetas = getViewName(selectedLayout);
            if (viewMetas != null && viewMetas.length != 0) {
                setView(viewMetas[0]);
            }
            ++selectedLayout;
            return viewMetas;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] getViewName(int selectedLayoutInner) throws JSONException {
        selectedLayout = getSelectedLayoutPosition(selectedLayoutInner, MyConstants.SELECTED_TEMPLATE_SEQ);
        if (templateSeqJsonArray != null && templateSeqJsonArray.length() != 0) {
            return new String[]{templateSeqJsonArray.getJSONObject(selectedLayout).getString("TemplateName"),
                    templateSeqJsonArray.getJSONObject(selectedLayout).getString("Duration")};

        }

        return null;
    }

    private int getSelectedLayoutPosition(int selectedLayoutInner, int typeArray) {
        if (typeArray == MyConstants.SELECTED_TEMPLATE_SEQ) {
            if (selectedLayoutInner == -1 || (templateSeqJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        } else if (typeArray == MyConstants.SELECTED_SCHEDULE) {
            if (selectedLayoutInner == -1 || (adFullScheduleJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        } else if (typeArray == MyConstants.SELECTED_SCHEDULE_NORMAL) {
            if (selectedLayoutInner == -1 || (normalSchedulesJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        }

        return selectedLayoutInner;
    }

    public void initialize() {
        try {

            branchName.setText(Methods.getBranchName(this));
            templateSeqJsonArray = new JSONArray(Methods.getSharedPref(this).getString("sequence", ""));
            adFullScheduleJsonArray = new JSONArray();
            adHocStatus = MyConstants.ADH_NOT_LOADED;
//            isDownloadStarted = false;

            configNormalSchedules();
            configAdFullSchedules(null);
//            if (dynamic.getChildCount() == 0) {
            loadLayouts(MyValues.isResetApp());
//            }
            hideAllLayouts();
            setNewsLines();
            loadWebView();
            resetHandlersAndReInit();

            selectedLayout = -1;
            selectedSchedule = -1;
            selectedScheduleNormal = -1;
            Date startDate = simpleDateFormatTime.parse(Methods.getSharedPref(this).getString("DownloadTime", ""));
            downloadStartTime = new Date[]{startDate, add10Sec(startDate)};


            if (!MyValues.isResetApp()) {
                initializeHandlers();
                checkDownloadOnStart(Methods.getSharedPref(this).getString("DownloadStop", ""));
                Log.d(TAG, "initialize: -------111222333" + MyValues.isResetApp());
            }

            MyValues.setResetApp(false);
            Log.d(TAG, "initialize: =============================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDownloadOnStart(String downloadStop) {
        try {
            Date date = new Date();
            if (simpleDateFormatTime.parse(simpleDateFormatTime.format(date)).before(simpleDateFormatTime.parse(downloadStop))) {
                beginDownload();

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void resetHandlersAndReInit() {
        if (handlerChangeBoolean != null && runnableChangeBoolean != null) {
            handlerChangeBoolean.removeCallbacks(runnableChangeBoolean);
        }
        if (handlerSingleSecond != null && runnableSingleSecond != null) {
            handlerSingleSecond.removeCallbacks(runnableSingleSecond);
            handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);

        }
        if (handlerSchedules != null && runnableSingleSecond != null) {
            handlerSchedules.removeCallbacks(runnableSchedules);
            handlerSchedules.postDelayed(runnableSchedules, 1500);

        }
        if (handlerTemplateSequence != null && runnableTemplateSequence != null) {
            handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
            handlerTemplateSequence.postDelayed(runnableTemplateSequence, 1000);

        }
        if (handlerCatchFullPlayed != null && runnableCatchFullPlayed != null) {
            handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);

        }
    }


    public void beginDownload() {
        String url = Refferences.GetDownloadMedia.methodName + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + Methods.getNodeId(FullscreenActivity.this);
        new AsyncWebService(this, MyConstants.GET_REQUIRED_FILES).execute(url);
    }

    private void loadLayouts(boolean resetApp) {
//        try {
        if (adFullView == null) {
//                for (int i = 0; i < templateSeqJsonArray.length(); i++) {
//                    if (templateSeqJsonArray.getJSONObject(i).getString("TemplateName").equalsIgnoreCase("AdFullV")) {
            dynamic.addView(adFullView = inflater.inflate(R.layout.layout_ad_full, null));

            imageViewAdFull = adFullView.findViewById(R.id.img_MyFullLayoutAdFull);
            videoViewAdFull = adFullView.findViewById(R.id.vid_MyFullLayoutAdFull);
            videoViewAdFull.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    handlerSchedules.postDelayed(runnableSchedules, 10);
                    Log.d(TAG, "onError: VidAdFull++++++++++++++");
                    return true;
                }
            });
//            videoViewAdFull.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mpAdFull = mediaPlayer;
//                }
//            });               14:27:07.311
//                        break;
//                    }
//                }
        }
        if (!resetApp && currencyView == null) {
            dynamic.addView(currencyView = inflater.inflate(R.layout.layout_currency_v, null));

            imageViewNormal = currencyView.findViewById(R.id.imgV_ImageLayoutCurrencyV);
            videoViewNormal = currencyView.findViewById(R.id.vidV_VideoLayoutCurrencyV);
            videoViewNormal.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    ++selectedScheduleNormal;
                    handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);
                    handlerSchedules.postDelayed(runnableSchedules, 1);
//                    Uri mUri = null;
//                    try {
//                        Field mUriField = VideoView.class.getDeclaredField("mUri");
//                        mUriField.setAccessible(true);
//                        mUri = (Uri) mUriField.get(videoViewNormal);
//                        Log.d(TAG, "onError: VidNormal++++++++++++++ " + mUri.toString());
//                        Log.d(TAG, "onError: VidNormal++++++++++++++ " + mUri.toString());
//                    } catch (Exception e) {
//                    }

                    return true;
                }
            });
//            videoViewNormal.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mpNormal = mediaPlayer;
//                }
//            });


            currencyMediaWrapper = currencyView.findViewById(R.id.rl_mediaWrapperLayoutCurrencyV);
            hdmiInRelative = currencyView.findViewById(R.id.home_ac_hdmi);
            mRealtekeHdmi = new ZidooHdmiDisPlay_(FullscreenActivity.this, hdmiInRelative, null, ZidooHdmiDisPlay_.TYPE_SURFACEVIEW);
            mRealtekeHdmi.startDisPlay();
        }

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void loadWebView() {
        if (currencyView != null) {
            WebView webView = currencyView.findViewById(R.id.webV_ProductLayoutCurrencyV);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCacheEnabled(false);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.loadUrl(Refferences.GetProduct.method);

            webView = currencyView.findViewById(R.id.webV_CurrencyLayoutCurrencyV);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCacheEnabled(false);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.loadUrl(Refferences.GetCurrency.method + Methods.getNodeId(FullscreenActivity.this));
        }
    }

    public void setNewsLines() {
        new AsyncWebService(this, MyConstants.GET_NEWS).execute(Refferences.GetNews.methodName + Methods.getNodeId(FullscreenActivity.this));
    }


    private void configNormalSchedules() {
        try {
            normalSchedulesJsonArray = new JSONArray();
            JSONArray schedulesJsonArray = new JSONArray(Methods.getSharedPref(this).getString("schedules", ""));
            JSONArray jsonArray;

            for (int i = 0; i < schedulesJsonArray.length(); i++) {
                if (schedulesJsonArray.getJSONObject(i).getInt("Panel") == 0) {
                    jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Default");
                    for (int l = 0; l < jsonArray.length(); l++) {
                        normalSchedulesJsonArray.put(jsonArray.getJSONObject(l));
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String configAdFullSchedules(Date now) {
        String playId = "";
        try {
            boolean isDefault = true;
//            if (adFullScheduleJsonArray.length() != 0) {
            adFullScheduleJsonArray = new JSONArray();
//            adHocStopTimeArrayList = new ArrayList<>();
            adHocTimeArrayList = new ArrayList<>();
            Log.d(TAG, "configAdFullSchedules: " + 1);
            JSONArray schedulesJsonArray = new JSONArray(Methods.getSharedPref(this).getString("schedules", ""));
            JSONArray jsonArray;
            for (int i = 0; i < schedulesJsonArray.length(); i++) {
                Log.d(TAG, "configAdFullSchedules: " + 2);

                if (schedulesJsonArray.getJSONObject(i).getInt("Panel") == 2) {
                    jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Schedule");
                    Log.d(TAG, "configAdFullSchedules: " + jsonArray.length() + "jLength");
//                    if (jsonArray.length() != 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObjectInner = jsonArray.getJSONObject(j);

                        if (now == null) {
                            now = getDateFromString(simpleDateFormatTime.format(new Date()));
                        }

                        Date start = getDateFromString(jsonObjectInner.getString("Start"));
                        Date end = getDateFromString(jsonObjectInner.getString("End"));
                        Log.d(TAG, "configAdFullSchedules: start" + start);
                        if (now.equals(start) || (now.after(start) && now.before(add10Sec(start)))) {
                            for (int k = 0; k < jsonObjectInner.getJSONArray("PlayItems").length(); k++) {
                                adFullScheduleJsonArray.put(jsonObjectInner.getJSONArray("PlayItems").getJSONObject(k));
                            }
                            isDefault = false;
//                            if (now.equals(start)) {
                            playId = jsonObjectInner.getString("PlaylistScheduleId");
//                            }
                        }

//                        adHocStartTimeArrayList.add(getDateArray(start));
//                        adHocStopTimeArrayList.add(getDateArray(end));
                        adHocTimeArrayList.add(getDateArray(start, end));
                        Log.d(TAG, "configAdFullSchedules: " + start);
                        Log.d(TAG, "configAdFullSchedules: " + end);
                    }
//                    }
                    if (isDefault) {
                        jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Default");
                        for (int l = 0; l < jsonArray.length(); l++) {
                            adFullScheduleJsonArray.put(jsonArray.getJSONObject(l));
                        }
//                        scheduleAdFullConfigs(MyConstants.DEFAULT_ARRAY, "");
                    }

                    break;
                }

            }
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return playId;
    }

    private Date[] getDateArray(Date start, Date end) {
        return new Date[]{start, add10Sec(start), end, add10Sec(end)};
    }

    private Date add10Sec(Date date) {
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 10);
        return calendar.getTime();
    }

    //
//    private void scheduleAdFullConfigs(String typeArray, String playlistScheduleId) {
//        if (adFullArray != null) {
//            if (!adFullArray[0].equalsIgnoreCase(typeArray)) {
//                adFullArray[0] = typeArray;
//                adFullArray[1] = playlistScheduleId;
//                selectedSchedule = -1;
//
//            } else if (adFullArray[0].equalsIgnoreCase(MyConstants.SCHEDULE_ARRAY) && !adFullArray[1].equalsIgnoreCase(playlistScheduleId)) {
//                adFullArray[1] = playlistScheduleId;
//                selectedSchedule = -1;
//            }
//        } else {
//            adFullArray = new String[]{typeArray, playlistScheduleId};
//            selectedSchedule = -1;
//        }
//    }

    private Date getDateFromString(String format) throws ParseException {
        return simpleDateFormatTime.parse(format);
    }


    private void setView(String viewName) {
        if (viewName.equalsIgnoreCase("AdFullV")) {
//            adFullView.setAlpha(1);
            videoViewAdFull.setVisibility(View.VISIBLE);
            currencyView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
            adFullView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));

        } else {
            if (currentLayoutDetails != null && currentLayoutDetails[0].equalsIgnoreCase("AdFullV")) {
                videoViewAdFull.stopPlayback();
                videoViewAdFull.seekTo(0);
                videoViewAdFull.setVisibility(View.GONE);
//                if (mpAdFull != null) {
//                    mpAdFull.release();
//                }
                if (currencyView != null) {
                    currencyView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                } else {
                    return;
                }
                adFullView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
            } else {
//                adFullView.setAlpha(0);
                if (currencyView.getAnimation() != null) {
                    currencyView.getAnimation().reset();
                }

            }

        }
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//               Synchronized Handlers

    //    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
    private synchronized void methodTemplateSeq() {
        handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
        currentLayoutDetails = loadLayout();

        if (handlerCatchFullPlayed != null && runnableCatchFullPlayed != null) {
            handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);
        }
        if (currentLayoutDetails != null && currentLayoutDetails.length != 0) {
            if (currentLayoutDetails[0].equalsIgnoreCase("AdFullV")) {
                handlerChangeBoolean.postDelayed(runnableChangeBoolean, getMillisFromTime(currentLayoutDetails[1]));
                handlerSchedules.postDelayed(runnableSchedules, 10);
                mRealtekeHdmi.setAudio(false);
                return;

            } else {
                loadLayoutSchedules();
            }

            handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
            handlerSchedules.postDelayed(runnableSchedules, 10);
        }
    }

    private synchronized void methodSchedules() {
        try {
            handlerSchedules.removeCallbacks(runnableSchedules);
            if (dynamic.getChildCount() != 0) {
                Log.d(TAG, "run: *************" + currentLayoutDetails[0]);
                if (currentLayoutDetails[0].equalsIgnoreCase("AdFullV")) {
                    if (changeAdFull) {
                        currentLayoutDetails = loadLayout();
                        loadLayoutSchedules();
                        handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
                        handlerSchedules.postDelayed(runnableSchedules, 1);
                        changeAdFull = false;
                    } else {
//                                configAdFullSchedules(null);
                        selectedSchedule = getSelectedLayoutPosition(selectedSchedule, MyConstants.SELECTED_SCHEDULE);
                        jsonObjectInsideRunnableSchedules = adFullScheduleJsonArray.getJSONObject(selectedSchedule);
                        String fileName = jsonObjectInsideRunnableSchedules.getString("Name");


                        file = new File(Methods.createOrGetDirectory().toString() + "/" + jsonObjectInsideRunnableSchedules.getString("Name"));
                        Log.d(TAG, "run: " + fileName);
                        if (file.exists()) {
                            if (imageVideoConfigsWithIsImage(file)) {
                                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                videoViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
                                imageViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                                imageViewAdFull.setImageBitmap(bitmap);
                            } else {
//                                        videoView.setVisibility(View.VISIBLE);
                                imageViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
                                videoViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                                videoViewAdFull.setVideoURI(Uri.parse(file.toString()));
                                videoViewAdFull.start();
                                imageViewAdFull.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        imageViewAdFull.setImageResource(0);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                            }


                            Log.d(TAG, "run: ===========" + getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                            handlerSchedules.postDelayed(runnableSchedules, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                        } else {
                            handlerSchedules.postDelayed(runnableSchedules, 10);
                        }
                        ++selectedSchedule;
//                                if (jsonObject.getString("Name"))
                    }
                } else if (currentLayoutDetails[0].equalsIgnoreCase("CurrencySplitV")) {
//                            configNormalSchedules();
                    selectedScheduleNormal = getSelectedLayoutPosition(selectedScheduleNormal, MyConstants.SELECTED_SCHEDULE_NORMAL);
                    jsonObjectInsideRunnableSchedules = normalSchedulesJsonArray.getJSONObject(selectedScheduleNormal);
                    String fileName = jsonObjectInsideRunnableSchedules.getString("Name");


                    file = new File(Methods.createOrGetDirectory().toString() + "/" + jsonObjectInsideRunnableSchedules.getString("Name"));
                    Log.d(TAG, "run:---------- " + fileName);
                    if (file.exists()) {
                        if (imageVideoConfigsWithIsImage(file)) {
                            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                                    videoViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
//                                    imageViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                            imageViewNormal.setImageBitmap(bitmap);
                            imageViewNormal.setVisibility(View.VISIBLE);
                            videoViewNormal.setVisibility(View.GONE);
                        } else {
//                                    imageViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
//                                    videoViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                            videoViewNormal.setVideoURI(Uri.parse(file.toString()));
                            videoViewNormal.setVisibility(View.VISIBLE);
                            imageViewNormal.setVisibility(View.GONE);
//                                    videoViewNormal.get
                            videoViewNormal.start();

                        }

//asd
                        handlerCatchFullPlayed.postDelayed(runnableCatchFullPlayed, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                        handlerSchedules.postDelayed(runnableSchedules, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                    } else {
                        handlerSchedules.postDelayed(runnableSchedules, 1);
                    }
//                            ++selectedScheduleNormal;
                }

//                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                        videoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//
//
            } else {
                handlerSchedules.postDelayed(runnableSchedules, 1000);
                Log.d(TAG, "run:  else----------");
            }
//                    handlerSchedules.postDelayed(runnableSchedules, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void methodSingleSec() {
        setTimePlusCheckDownloadingTimePlusAdHoc();
        currentSecond++;
        setNetStatusImage();
        setHeartBeatPacket();

        handlerSingleSecond.removeCallbacks(runnableSingleSecond);
        handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);
    }

//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------

    public long getMillisFromTime(String dateString) {
        String[] splitStrings = dateString.split(":");
        return ((Long.parseLong(splitStrings[0]) * 60 * 60) + (Long.parseLong(splitStrings[1]) * 60) + (Long.parseLong(splitStrings[2]))) * 1000;
    }
}
