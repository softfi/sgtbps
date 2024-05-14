package com.sgtbps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sgtbps.students.StudentDashboard;
import com.sgtbps.utils.Constants;
import com.sgtbps.utils.Utility;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 1000;
    ImageView logoIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);

        }
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        logoIV = findViewById(R.id.splash_logo);

        Boolean isLocaleSet;

        try {
            isLocaleSet = Utility.getSharedPreferencesBoolean(getApplicationContext(), "isLocaleSet");
        } catch (NullPointerException e) {
            isLocaleSet = false;
        }

        if(isLocaleSet) {
            setLocale(Utility.getSharedPreferences(getApplicationContext(), Constants.langCode));
        }
        splash();
    }

    private void splash() {

        new Handler().postDelayed(new Runnable() {
            public void run() {
                boolean isLoggegIn;
                boolean isUrlTaken;

                try {
                    isLoggegIn = Utility.getSharedPreferencesBoolean(getApplicationContext(), Constants.isLoggegIn);
                    isUrlTaken = Utility.getSharedPreferencesBoolean(getApplicationContext(), "isUrlTaken");
                    Log.d("TAG123", "run: "+ Boolean.toString(isUrlTaken));
                } catch (NullPointerException NPE) {
                    isLoggegIn = false;
                    isUrlTaken = false;
                }

                Log.e("loggeg", Boolean.toString(isLoggegIn));
                Log.e("isUrlTaken", Boolean.toString(isUrlTaken));

                if(Constants.askUrlFromUser) {
                    Intent i;
                    if(isLoggegIn){
                        i = new Intent(getApplicationContext(), StudentDashboard.class);
                    }else {
                        i = new Intent(getApplicationContext(), Login.class);
                    }
                    startActivity(i);
                    finish();
//                    if(isUrlTaken) {
//                        if(isLoggegIn){
//                            Intent i = new Intent(getApplicationContext(), StudentDashboard.class);
//                            startActivity(i);
//                            finish();
//                        }else {
//                            Intent i = new Intent(getApplicationContext(), Login.class);
//                            startActivity(i);
//                            finish();
//                        }
//                    } else {
//                        Intent asd = new Intent(getApplicationContext(), TakeUrl.class);
//                        startActivity(asd);
//                        finish();
//                    }
                } else {
                    Intent i;
                    if(isLoggegIn){
                        i = new Intent(getApplicationContext(), StudentDashboard.class);
                    }else {
                        i = new Intent(getApplicationContext(), Login.class);
                    }
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public void setLocale(String localeName) {
        Locale myLocale = new Locale(localeName);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Log.e("Status", "Locale updated!");
    }
}