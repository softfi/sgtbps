package com.sgtbps;

import static android.widget.Toast.makeText;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sgtbps.utils.Constants;
import com.sgtbps.utils.MyApp;
import com.sgtbps.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class TakeUrl extends Activity {
    EditText urlET;
    Button submitBtn;
    Locale myLocale;
    String langCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.take_url_activity);

        urlET = findViewById(R.id.et_domain_takeUrl);
        submitBtn = findViewById(R.id.btn_submit_takeUrl);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appDomain = urlET.getText().toString();
                if(Utility.isConnectingToInternet(TakeUrl.this)){
                  //  getDataFromApi(appDomain);
                }else{
                    makeText(getApplicationContext(),R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }

               Utility.setSharedPreference(getApplicationContext(), Constants.appDomain, appDomain);
            }
        });
    }

    public void setLocale(String localeName) {

        if(localeName.isEmpty() || localeName.equals("null")) {
            localeName = "en";
            Log.e("localName status", "empty");
        }
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Log.e("Status", "Locale updated!");
        Utility.setSharedPreferenceBoolean(getApplicationContext(), Constants.isLocaleSet, true);
        Utility.setSharedPreference(getApplicationContext(), Constants.currentLocale, localeName);
    }

     private void getDataFromApi (String domain) {
         Log.d("domain", "getDataFromApi: "+domain.toString());
         final ProgressDialog pd = new ProgressDialog(this);
         pd.setMessage("Loading");
         pd.setCancelable(false);
         pd.show();

         if(!domain.endsWith("/")) {
             domain += "/";
         }

         final String url = "https://sgtbps.in/app";
         Log.d("url", "getDataFromApi: "+url.toString());

         StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
             @Override
             public void onResponse(String result) {
                 Log.d("Result", "getDataFromApi: "+result.toString());
                 if (result != null) {
                     pd.dismiss();
                     try {
                         JSONObject object = new JSONObject(result);
                         String success = "200"; //object.getString("status");
                         if (success.equals("200")) {
                             Utility.setSharedPreferenceBoolean(getApplicationContext(), "isUrlTaken", true);
                             Utility.setSharedPreference(MyApp.getContext(), Constants.apiUrl, object.getString("url"));
                             Utility.setSharedPreference(MyApp.getContext(), Constants.imagesUrl, object.getString("site_url"));
                             String app_ver= object.getString("app_ver");
                             Utility.setSharedPreference(getApplicationContext(), Constants.app_ver, app_ver);
                             String appLogo = object.getString("site_url") + "uploads/school_content/logo/app_logo/" + object.getString("app_logo");
                             Utility.setSharedPreference(MyApp.getContext(), Constants.appLogo, appLogo );

                             String secColour = object.getString("app_secondary_color_code");
                             String primaryColour = object.getString("app_primary_color_code");

                             if(secColour.length() == 7 && primaryColour.length() == 7) {
                                 Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, secColour);
                                 Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, primaryColour);
                             }else {
                                 Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, Constants.defaultSecondaryColour);
                                 Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, Constants.defaultPrimaryColour);
                             }
                             Log.e("apiUrl Utility", Utility.getSharedPreferences(getApplicationContext(), "apiUrl"));
                             langCode = object.getString("lang_code");
                             Utility.setSharedPreference(getApplicationContext(), Constants.langCode,langCode);

                             if(!langCode.isEmpty()) {
                                 setLocale(langCode);
                             }

                             Intent asd = new Intent(getApplicationContext(), Login.class);
                             startActivity(asd);
                             finish();

                         } else {
                             Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                         }
                     } catch (JSONException e) {
                             langCode = "";
                         e.printStackTrace();
                     }
                 } else {
                     pd.dismiss();
                     Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 pd.dismiss();
                 System.out.println("not responding");
                 try {
                     int  statusCode = error.networkResponse.statusCode;
                     NetworkResponse response = error.networkResponse;
                     Log.e("Volley Error",""+statusCode+" "+response.data.toString());
                     if(error instanceof ClientError) {
                         Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                     } else {
                         Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                     }
                 } catch (NullPointerException npe) {
                     Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
             }
             }
         });
         RequestQueue requestQueue = Volley.newRequestQueue(TakeUrl.this); //Creating a Request Queue
         requestQueue.add(stringRequest);//Adding request to the queue
     }
}
