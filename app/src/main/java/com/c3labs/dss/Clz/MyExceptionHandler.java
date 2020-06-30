package com.c3labs.dss.Clz;

/**
 * Created by c3 on 3/19/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Context myContext;
    private final Class<?> myActivityClass;

    public MyExceptionHandler(Context context, Class<?> c) {
        myContext = context;
        myActivityClass = c;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        restartApp(myContext, myActivityClass ,exception);
    }

    public static void restartApp(Context context, Class<?> myActivityClass, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);// You can use LogCat too
        Intent intent = new Intent(context, myActivityClass);
        String s = stackTrace.toString();
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        Log.d(";;;;;;;;;;;;;;;;;;;;", "uncaughtException: " + s);
        //you can use this String to know what caused the exception and in which Activity
        intent.putExtra("uncaughtException", "Exception is: " + stackTrace.toString());
        intent.putExtra("stacktrace", s);
        context.startActivity(intent);
        //for restarting the Activity
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
