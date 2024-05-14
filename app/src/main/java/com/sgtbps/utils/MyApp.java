package com.sgtbps.utils;

import android.app.Application;

public class MyApp extends Application {

    private static MyApp mContext;

    @Override
    public void onCreate() {
        super.onCreate();

       CustomFont.overrideFont(getApplicationContext(), "SANS", "fonts/roboto.ttf");
        mContext = this;
    }

    public static MyApp getContext() {
        return mContext;
    }

    public static String isNullEmpty(String str) {

        // check if string is null
        if (str == null) {
            return "NULL";
        }

        // check if string is empty
        else if(str.isEmpty()){
            return "EMPTY";
        }

        else {
            return "neither NULL nor EMPTY";
        }
    }
}
