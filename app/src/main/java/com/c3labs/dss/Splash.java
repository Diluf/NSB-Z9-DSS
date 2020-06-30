package com.c3labs.dss;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c3labs.dss.Clz.MyExceptionHandler;
import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.Recievers.MyBroadCastReciever;
import com.c3labs.dss.WebService.Asyncs.AsyncWebService;
import com.c3labs.dss.WebService.Refferences;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class Splash extends AppCompatActivity {
    private static final String TAG = "=====================";
    AlertDialog alertDialog;
    public TextView signInAgain;
    public TextView statusMessage;
    public RelativeLayout errorlayout;
    public ProgressBar progressBar;
    //    public VideoView videoView;
    public MyBroadCastReciever myBroadCastReciever;
    RelativeLayout noNet;
    ImageView logo, exclam;
    int netStateCount;
    public Handler netStatusHandler;
    public Runnable netStatusRunnable;
    public Handler handler03Min;
    public Runnable runnable03Min;
//    SharedPreferences prefs;

    public boolean crashApp;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unregReciever();
        netStatusHandler.removeCallbacks(netStatusRunnable);
        handler03Min.removeCallbacks(runnable03Min);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grantPermission("android.permission.CAMERA");
        grantPermission("android.permission.READ_EXTERNAL_STORAGE");
        grantPermission("android.permission.WRITE_EXTERNAL_STORAGE");

        setContentView(R.layout.activity_spalsh);


        signInAgain = findViewById(R.id.tv_SignInAgainActivitySplash);
        errorlayout = findViewById(R.id.rl_MessageDetails_activitySplash);
        statusMessage = findViewById(R.id.statusMessage);
        progressBar = findViewById(R.id.progressBar_ActivitySplash);
//        videoView = findViewById(R.id.vidV_LogoActivitySplash);
        noNet = findViewById(R.id.rl_NoNetworkActivitySplash);
        exclam = findViewById(R.id.imgV_exclamActivitySplash);
        logo = findViewById(R.id.splash_Logo);
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_out_all_splash_logo));

//        prefs = Methods.getSharedPref(this);
//        initSplashVideo();

        signInAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        initHandlers();
//        showLoginDialogOrContinue();
//        if (MyValues.isBootCompleted()) {
//            showLoginDialogOrContinue();
//        }
    }

    private void initHandlers() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "run: Granted");
                    registerReceiver(myBroadCastReciever = new MyBroadCastReciever(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                    handler.removeCallbacks(this);

                } else {
                    Log.d(TAG, "run: Else");
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1);

        netStatusHandler = new Handler();
        netStatusRunnable = new Runnable() {
            @Override
            public void run() {
                if (netStateCount >= 120) {
                    new Methods().restartDevice();
                } else {
                    if (MyBroadCastReciever.isNetworkConnected(Splash.this)) {
                        netStateCount = 0;
                    } else {
                        netStateCount++;
                    }
                }
                Log.d(TAG, "run: netStatus" + netStateCount);
                netStatusHandler.postDelayed(netStatusRunnable, 1000);
            }
        };
        netStatusHandler.postDelayed(netStatusRunnable, 1);

        handler03Min = new Handler();
        runnable03Min = new Runnable() {
            @Override
            public void run() {
                MyExceptionHandler.restartApp(Splash.this, Splash.class, new Exception());
            }
        };
        handler03Min.postDelayed(runnable03Min, 1000 * 60 * 03);

    }

    private void grantPermission(String permission) {
        try {
            String cmd = "pm grant " + getPackageName() + " " + permission + "\n";

            Process root = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(root.getOutputStream());
            Log.d("APK---", "onClick: " + cmd);
            os.writeBytes(cmd);

            os.flush();

//            int i1 = root.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void initSplashVideo() {
////        findViewById(R.id.imgV_LogoActivitySplash).startAnimation(AnimationUtils.loadAnimation(Splash.this, R.anim.fade_all_time));
//        String path = "android.resource://" + getPackageName() + "/" + R.raw.load;
////        videoView.setVideoURI(Uri.parse(path));
////        videoView.start();
//
//
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
//
//    }

    public void showLoginDialogOrContinue() {
        if (MyBroadCastReciever.isNetworkConnected(this)) {
            viewNoNetwork(false);
//            checkAndRequestPermissions();
            File file = new File(Methods.createOrGetDirectory().toString() + "/loginfile.txt");
            if (file.exists()) {
                readFileAndExecute(file);
            } else {
                showLoginDialog();
                Log.d(TAG, "showLoginDialogOrContinue: File Not Existed.!");
            }
        } else {
            viewNoNetwork(true);
            if (crashApp) {
                MyExceptionHandler.restartApp(this, Splash.class, new Exception());
            }
        }
    }

    private void viewNoNetwork(boolean isNoNet) {
//        splashLogo(isNoNet);
        if (isNoNet) {
            statusMessage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            noNet.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            exclam.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_all_time));
            doIndeterminate(false);
        } else {
            statusMessage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            if (noNet.getAnimation() != null) {
//                exclam.getAnimation().cancel();
//                exclam.getAnimation().reset();
                exclam.clearAnimation();
                noNet.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            }
            doIndeterminate(true);
        }
    }

//    private void splashLogo(boolean isNoNet) {
//        Log.d(TAG, "splashLogo: " + isNoNet);
//        if (isNoNet) {
//            if (logo.getAnimation() != null) {
//                logo.getAnimation().reset();
//                logo.getAnimation().cancel();
//            }
//        } else {
//            logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_out_all_splash_logo));
//        }
//    }

    private void readFileAndExecute(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            ArrayList<String> arrayList = new ArrayList();
            for (String line; (line = br.readLine()) != null; ) {
                arrayList.add(line);
            }
            if (arrayList.size() >= 2) {
                String path = Refferences.Login.methodName + arrayList.get(0) + "/" + arrayList.get(1);
                doIndeterminate(true);
                new AsyncWebService(Splash.this, MyConstants.LOGIN).execute(path);
            } else {
                showLoginDialog();
            }
            // line is not visible here.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doIndeterminate(boolean isStart) {
        if (isStart) {
            if (!progressBar.isIndeterminate()) {
                progressBar.setIndeterminate(true);
            }
        } else {
            if (progressBar.isIndeterminate()) {
                progressBar.setIndeterminate(false);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                for (String perm :
                        permissions) {
                    Log.d(TAG, "onRequestPermissionsResult: " + perm + " ------ " + (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED));
                    if ((ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED)) {
                        unregReciever();
                        finish();
                        break;
                    }
                }

                break;
        }

    }

    public void showLoginDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.dialog_signin, null);
        ImageView asterikL = v.findViewById(R.id.imgV_asterikLeftActivitySplash);
        ImageView asterik = v.findViewById(R.id.imgV_asterikActivitySplash);
        ImageView asterikR = v.findViewById(R.id.imgV_asterikRightActivitySplash);
        final EditText userName = v.findViewById(R.id.et_dialog_signin_UserName);
        final EditText password = v.findViewById(R.id.et_dialog_signin_Password);
        Button exit = v.findViewById(R.id.btn_dialog_signin_Exit);
        Button signIn = v.findViewById(R.id.btn_dialog_signin_SignIn);

        asterik.startAnimation(AnimationUtils.loadAnimation(this, R.anim.load_asterik));
        asterikL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        asterikR.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        alertDialog = new AlertDialog.Builder(this).setView(v).create();
        alertDialog.setCancelable(false);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregReciever();
                finish();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyBroadCastReciever.isNetworkConnected(Splash.this)) {
                    if (!userName.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()) {
                        alertDialog.dismiss();

                        String path = Refferences.Login.methodName + userName.getText().toString().trim() + "/" + password.getText().toString().trim();
                        doIndeterminate(true);
                        new AsyncWebService(Splash.this, MyConstants.LOGIN).execute(path);

//                    Write to file--------
                        try {
                            PrintWriter writer = new PrintWriter(Methods.createOrGetDirectory().toString() + "/loginfile.txt", "UTF-8");
                            writer.println(userName.getText().toString().trim());
                            writer.println(password.getText().toString().trim());
                            writer.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (errorlayout.getAnimation() != null) {
                            errorlayout.startAnimation(AnimationUtils.loadAnimation(Splash.this, R.anim.fade_out));
                            errorlayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    errorlayout.setVisibility(View.INVISIBLE);
                                    signInAgain.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
//                    loaderWrapper.startAnimation(AnimationUtils.loadAnimation(Splash.this, R.anim.fade_in));
                    } else {
                        Toast.makeText(Splash.this, "Invalid User Name or Password", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        alertDialog.show();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    public void unregReciever() {
        if (myBroadCastReciever != null) {
            unregisterReceiver(myBroadCastReciever);
            myBroadCastReciever = null;
        }
    }


}
