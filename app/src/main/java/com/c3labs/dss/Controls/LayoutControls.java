package com.c3labs.dss.Controls;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.c3labs.dss.R;

/**
 * Created by c3 on 2/8/2018.
 */

public class LayoutControls {
    public View getRequiredLayout(LayoutInflater inflater, Context context, int layout) {
        Log.d("pppp-------", "getRequiredLayout: " +layout);

        View v = inflater.inflate(layout, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));

        return v;

    }




//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------
//    -------------------------------------------------------------

    public int getRequiredLayoutInt(String templateName) {
        if (templateName.equalsIgnoreCase("AdFull")) {
            return R.layout.layout_ad_full;
        } else if (templateName.equalsIgnoreCase("CurrencySplit")) {
            return R.layout.layout_currency_split;
        } else if (templateName.equalsIgnoreCase("Web")) {
            return R.layout.layout_web;
        } else if (templateName.equalsIgnoreCase("CurrencyHDMIV")) {
            return R.layout.layout_currency_v;
        } else if (templateName.equalsIgnoreCase("CurrencySplitV")) {
            return R.layout.layout_currency_split_v;
        }

        return 0;
    }

    public long getMillisFromTime(String dateString) {
        String[] splitStrings = dateString.split(":");
        return ((Long.parseLong(splitStrings[0]) * 60 * 60) + (Long.parseLong(splitStrings[1]) * 60) + (Long.parseLong(splitStrings[2]))) * 1000;
    }
}
