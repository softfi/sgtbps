package com.sgtbps;

import static android.widget.Toast.makeText;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sgtbps.adapters.LoginChildListAdapter;
import com.sgtbps.retrofit.apiInterface;
import com.sgtbps.students.StudentDashboard;
import com.sgtbps.utils.Constants;
import com.sgtbps.utils.MyApp;
import com.sgtbps.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends Activity {

    Locale myLocale;

    TextView tv_forgotPass /*privacyTV*/;
    Button btn_login;

    String langCode = "";
    EditText et_userName, et_password;
    //LinearLayout changeUrlBtn;
    ImageView btn_showPassword, usernameIcon, passwordIcon;
    boolean isPasswordVisible = false;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    ArrayList<String> childNameList = new ArrayList<String>();
    ArrayList<String> childIdList = new ArrayList<String>();
    ArrayList<String> childImageList = new ArrayList<String>();
    ArrayList<String> childClassList = new ArrayList<String>();
    String device_token;
    String formData;
    ArrayAdapter<String> childListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_page);

        getUrl("https://sgtbps.in/");
        //getUrl("https://schoolingpro.in/admin/");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Login.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                device_token = FirebaseInstanceId.getInstance().getToken();
                Log.e("DEVICE TOKEN", device_token);
                System.out.println("DEVICE TOKEN=" + device_token);
            }
        });

        tv_forgotPass = (TextView) findViewById(R.id.tv_passwordReset_login);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_userName = (EditText) findViewById(R.id.et_username_login);
        et_password = (EditText) findViewById(R.id.et_password_login);

        btn_showPassword = (ImageView) findViewById(R.id.login_password_visibleBtn);
        usernameIcon = (ImageView) findViewById(R.id.icon_username_login);
        passwordIcon = (ImageView) findViewById(R.id.icon_password_login);

        // logoIV = (ImageView) findViewById(R.id.login_logo);
//
//        changeUrlBtn = (LinearLayout) findViewById(R.id.btn_changeUrl_login);

        //privacyTV = (TextView) findViewById(R.id.login_privacyTV);

       /* privacyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String domain = Utility.getSharedPreferences(getApplicationContext(), Constants.appDomain);
                System.out.println(" BEFORE PRIVACY URL" + domain);
                if (!domain.endsWith("/")) {
                    domain += "/";
                }
                System.out.println("PRIVACY URL" + domain);
                domain += Constants.privacyPolicyUrl;
                System.out.println("PRIVACY URL" + domain);

                Log.e("PRIVACY URL", domain);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(domain));
                startActivity(browserIntent);
            }
        });*/

        String appLogo = Utility.getSharedPreferences(this, Constants.appLogo) + "?" + new Random().nextInt(11);
        //Picasso.with(getApplicationContext()).load(appLogo).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(logoIV);
        childListAdapter = new ArrayAdapter<String>(Login.this, android.R.layout.select_dialog_singlechoice);

//
//        if (!Constants.askUrlFromUser) {
//           // changeUrlBtn.setVisibility(View.GONE);
//            if (Utility.isConnectingToInternet(Login.this)) {
//                getUrl("https://schoolingpro.in/admin/");
//                Utility.setSharedPreference(getApplicationContext(), Constants.appDomain, Constants.domain);
//            } else {
//                makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
//            }
//        }

        if (Constants.isDemoModeOn) {
            et_userName.setText("std1");
            et_password.setText("7jr142");
        }

        btn_showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isPasswordVisible) {
                    et_password.setTransformationMethod(null);
                    btn_showPassword.setImageResource(R.drawable.eye_black);
                    isPasswordVisible = true;
                } else {
                    et_password.setTransformationMethod(new PasswordTransformationMethod());
                    btn_showPassword.setImageResource(R.drawable.eye_grey);
                    isPasswordVisible = false;
                }
            }
        });

        tv_forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_userName.getText().toString();
                String password = et_password.getText().toString();
                if (username.isEmpty()) {
                    et_userName.setError("Please enter your registered username");
                } else if (password.isEmpty()) {
                    et_password.setError("Please Enter Password");
                } else {
                    if (Utility.isConnectingToInternet(Login.this)) {
                        params.put("username", username);
                        params.put("password", password);
                        params.put("deviceToken", device_token);
                        JSONObject obj = new JSONObject(params);
                        Log.e("params ", obj.toString());
                        postData(obj.toString());
                        //postData1(obj);
                    } else {
                        makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        changeUrlBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", false);
//                Utility.setSharedPreferenceBoolean(getApplicationContext(), "isUrlTaken", false);
//                Intent url = new Intent(getApplicationContext(), TakeUrl.class);
//                startActivity(url);
//                finish();
//            }
//        });

        //DECORATE//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.textHeading));
        }
        //DECORATE//
    }

    private void getUrl(String domain) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        if (!domain.endsWith("/")) {
            domain += "/";
        }

        final String url = domain + "app";
        Log.e("Verification Url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.e("Result", result);
                if (result != null) {
                    pd.dismiss();
                    try {
                        JSONObject object = new JSONObject(result);
                        String success = "200"; //object.getString("status");
                        if (success.equals("200")) {
                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "isUrlTaken", true);
                            Utility.setSharedPreference(MyApp.getContext(), Constants.apiUrl, object.getString("url"));
                            Utility.setSharedPreference(MyApp.getContext(), Constants.imagesUrl, object.getString("site_url"));
                            String app_ver = object.getString("app_ver");
                            Utility.setSharedPreference(getApplicationContext(), Constants.app_ver, app_ver);
                            String appLogo = object.getString("site_url") + "uploads/school_content/logo/app_logo/" + object.getString("app_logo");
                            Utility.setSharedPreference(MyApp.getContext(), Constants.appLogo, appLogo);
                            //Picasso.with(getApplicationContext()).load(appLogo).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(logoIV);
                            String secColour = object.getString("app_secondary_color_code");
                            String primaryColour = object.getString("app_primary_color_code");

                            if (secColour.length() == 7 && primaryColour.length() == 7) {
                                Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, secColour);
                                Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, primaryColour);
                            } else {
                                Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, Constants.defaultSecondaryColour);
                                Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, Constants.defaultPrimaryColour);
                            }
                            Log.e("apiUrl Utility", Utility.getSharedPreferences(getApplicationContext(), "apiUrl"));

                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
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
                try {
                    int statusCode = error.networkResponse.statusCode;
                    NetworkResponse response = error.networkResponse;

                    Log.e("Volley Error", statusCode + " " + response.data.toString());
                    if (error instanceof ClientError) {
                        Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException npe) {
                    Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);//Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
        // queue.add(request);
    }

    private void postData(String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.loginUrl;
        Log.d("URL===>", url + "===Request Body" + requestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "LoginReponse: " + response);
                if (response != null) {
                    pd.dismiss();
                    JSONObject object = null;
                    try {
                        object = new JSONObject(String.valueOf(response));
                        String success = object.getString("status");
                        String message = object.getString("message");
                        if (success.equals("1")) {
                            Utility.setSharedPreference(getApplicationContext(), Constants.loginType, object.getString("role"));
                            JSONObject data = object.getJSONObject("record");
                            Utility.setSharedPreference(getApplicationContext(), Constants.userId, object.getString("id"));
                            Utility.setSharedPreference(getApplicationContext(), "accessToken", object.getString("token"));
                            Utility.setSharedPreference(getApplicationContext(), "schoolName", data.getString("sch_name"));
                            Utility.setSharedPreference(getApplicationContext(), Constants.currency, data.getString("currency_symbol"));
                            Utility.setSharedPreference(getApplicationContext(), "startWeek", data.getString("start_week"));
                            String dateFormat = data.getString("date_format");
                            dateFormat = dateFormat.replace("m", "MM");
                            dateFormat = dateFormat.replace("d", "dd");
                            dateFormat = dateFormat.replace("Y", "yyyy");
                            System.out.println("dateFormat===" + dateFormat);
                            Utility.setSharedPreference(getApplicationContext(), "dateFormat", dateFormat);
                            String datesFormat = data.getString("date_format");
                            datesFormat = datesFormat.replace("m", "MM");
                            datesFormat = datesFormat.replace("d", "dd");
                            datesFormat = datesFormat.replace("Y", "yyyy");
                            String datetimeFormat = datesFormat + " " + "hh:mm aa";
                            System.out.println("datetimeFormat===" + datetimeFormat);
                            Utility.setSharedPreference(getApplicationContext(), "datetimeFormat", datetimeFormat);
                            Utility.setSharedPreference(getApplicationContext(), "language", data.getString("language"));
                            String imgUrl = Utility.getSharedPreferences(getApplicationContext(), "imagesUrl") + data.getString("image");
                            Utility.setSharedPreference(getApplicationContext(), Constants.userImage, imgUrl);
                            Utility.setSharedPreference(getApplicationContext(), Constants.userName, data.getString("username"));
                            Utility.setSharedPreference(getApplicationContext(), "schoolName", data.getString("sch_name"));
                            if (data.getString("role").equals("parent")) {
                                JSONArray childArray = data.getJSONArray("parent_childs");
                                if (childArray.length() == 1) {
                                    Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", true);
                                    Utility.setSharedPreferenceBoolean(getApplicationContext(), "hasMultipleChild", false);
                                    Utility.setSharedPreference(getApplicationContext(), Constants.studentId, childArray.getJSONObject(0).getString("student_id"));
                                    Utility.setSharedPreference(getApplicationContext(), Constants.classSection, childArray.getJSONObject(0).getString("class") + " - " + childArray.getJSONObject(0).getString("section"));
                                    Utility.setSharedPreference(getApplicationContext(), "studentName", childArray.getJSONObject(0).getString("name"));
                                    Intent asd = new Intent(getApplicationContext(), StudentDashboard.class);
                                    startActivity(asd);
                                    finish();
                                } else {
                                    Utility.setSharedPreferenceBoolean(getApplicationContext(), "hasMultipleChild", true);
                                    childNameList.clear();
                                    childIdList.clear();
                                    childImageList.clear();
                                    childClassList.clear();
                                    childListAdapter.clear();
                                    for (int i = 0; i < childArray.length(); i++) {
                                        childNameList.add(childArray.getJSONObject(i).getString("name"));
                                        childIdList.add(childArray.getJSONObject(i).getString("student_id"));
                                        childImageList.add(childArray.getJSONObject(i).getString("image"));
                                        childClassList.add(childArray.getJSONObject(i).getString("class") + " - "
                                                + childArray.getJSONObject(i).getString("section"));
                                        childListAdapter.add(childArray.getJSONObject(i).getString("name"));
                                    }
                                    childListAdapter.notifyDataSetChanged();
                                    showChildList();
                                }

                                Log.e("CHILD ARRAY LENGTH", childArray.length() + "..");

                            } else if (data.getString("role").equals("student")) {
                                Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", true);
                                Utility.setSharedPreference(getApplicationContext(), Constants.classSection, data.getString("class") + " (" + data.getString("section") + ")");
                                Utility.setSharedPreference(getApplicationContext(), Constants.studentId, data.getString("student_id"));
                                Intent asd = new Intent(getApplicationContext(), StudentDashboard.class);
                                startActivity(asd);
                                finish();
                            }

                        } else {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", false);
                        }
                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error 1", volleyError.toString());
                Toast.makeText(Login.this, R.string.invalidUsername, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        //SETTING RETRY Policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showChildList() {
        View view = getLayoutInflater().inflate(R.layout.fragment_login_bottom_sheet, null);
        view.setMinimumHeight(500);

        TextView headerTV = view.findViewById(R.id.login_bottomSheet_header);
        ImageView crossBtn = view.findViewById(R.id.login_bottomSheet_crossBtn);
        RecyclerView childListView = view.findViewById(R.id.login_bottomSheet_listview);

        headerTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.secondaryColour)));
        headerTV.setText(getString(R.string.childList));

        Log.e("ImageList", childImageList.toString());
        Log.e("Class List", childClassList.toString());
        Log.e("ID List", childIdList.toString());

        LoginChildListAdapter adapter = new LoginChildListAdapter(Login.this, childIdList, childNameList, childClassList, childImageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        childListView.setLayoutManager(mLayoutManager);
        childListView.setItemAnimator(new DefaultItemAnimator());
        childListView.setAdapter(adapter);

        final BottomSheetDialog dialog = new BottomSheetDialog(Login.this);

        dialog.setContentView(view);
        dialog.show();

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }


    private void getDataFromApi () {
        Log.d("domain", "getDataFromApi: ");
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();


        final String url = "https://sgtbps.in/app";
        Log.d("url", "getDataFromApi: "+ url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.d("Result", "getDataFromApi: "+ result);
                if (result != null) {
                    pd.dismiss();
                    try {
                        JSONObject object = new JSONObject(result);
                        //object.getString("status");
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
                            setLocales(langCode);
                        }

                        Intent asd = new Intent(getApplicationContext(), Login.class);
                        startActivity(asd);
                        finish();

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
                    Log.e("Volley Error", statusCode+" "+response.data.toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this); //Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }
    public void setLocale(String localeName) {
        myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, Login.class);
        refresh.putExtra("currentLang", localeName);
        startActivity(refresh);
    }

    public void setLocales(String localeName) {

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

      /*  private void postData1(JSONObject bodyParams) {
        final ProgressDialog pd = new ProgressDialog(Login.this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.loginUrl;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, bodyParams, new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LOGINRESPONSE", "onResponse: "+response.toString());

                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.print(response);

                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();

                    }


                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }


            @Override
            public Map<String, String> getHeaders() {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(jsonObjectRequest);
    }*/

     /*   public void loginData1() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final JSONObject body = new JSONObject();
        try {
            body.put("username", et_userName.getText().toString());
            body.put("password", et_password.getText().toString());
            body.put("deviceToken", device_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.loginUrl;
        Log.d("loginURL", "getDataFromApi: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            pd.dismiss();
                            JSONObject object = null;
                            try {
                                object = new JSONObject(String.valueOf(response));
                                String success = object.getString("status");
                                String message = object.getString("message");
                                if (success.equals("1")) {
                                    Utility.setSharedPreference(getApplicationContext(), Constants.loginType, object.getString("role"));
                                    JSONObject data = object.getJSONObject("record");
                                    Utility.setSharedPreference(getApplicationContext(), Constants.userId, object.getString("id"));
                                    Utility.setSharedPreference(getApplicationContext(), "accessToken", object.getString("token"));
                                    Utility.setSharedPreference(getApplicationContext(), "schoolName", data.getString("sch_name"));
                                    Utility.setSharedPreference(getApplicationContext(), Constants.currency, data.getString("currency_symbol"));
                                    Utility.setSharedPreference(getApplicationContext(), "startWeek", data.getString("start_week"));
                                    String dateFormat = data.getString("date_format");
                                    dateFormat = dateFormat.replace("m", "MM");
                                    dateFormat = dateFormat.replace("d", "dd");
                                    dateFormat = dateFormat.replace("Y", "yyyy");
                                    System.out.println("dateFormat===" + dateFormat);
                                    Utility.setSharedPreference(getApplicationContext(), "dateFormat", dateFormat);
                                    String datesFormat = data.getString("date_format");
                                    datesFormat = datesFormat.replace("m", "MM");
                                    datesFormat = datesFormat.replace("d", "dd");
                                    datesFormat = datesFormat.replace("Y", "yyyy");
                                    String datetimeFormat = datesFormat + " " + "hh:mm aa";
                                    System.out.println("datetimeFormat===" + datetimeFormat);
                                    Utility.setSharedPreference(getApplicationContext(), "datetimeFormat", datetimeFormat);
                                    Utility.setSharedPreference(getApplicationContext(), "language", data.getString("language"));
                                    String imgUrl = Utility.getSharedPreferences(getApplicationContext(), "imagesUrl") + data.getString("image");
                                    Utility.setSharedPreference(getApplicationContext(), Constants.userImage, imgUrl);
                                    Utility.setSharedPreference(getApplicationContext(), Constants.userName, data.getString("username"));
                                    Utility.setSharedPreference(getApplicationContext(), "schoolName", data.getString("sch_name"));
                                    if (data.getString("role").equals("parent")) {
                                        JSONArray childArray = data.getJSONArray("parent_childs");
                                        if (childArray.length() == 1) {
                                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", true);
                                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "hasMultipleChild", false);
                                            Utility.setSharedPreference(getApplicationContext(), Constants.studentId, childArray.getJSONObject(0).getString("student_id"));
                                            Utility.setSharedPreference(getApplicationContext(), Constants.classSection, childArray.getJSONObject(0).getString("class") + " - " + childArray.getJSONObject(0).getString("section"));
                                            Utility.setSharedPreference(getApplicationContext(), "studentName", childArray.getJSONObject(0).getString("name"));
                                            Intent asd = new Intent(getApplicationContext(), StudentDashboard.class);
                                            startActivity(asd);
                                            finish();
                                        } else {
                                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "hasMultipleChild", true);
                                            childNameList.clear();
                                            childIdList.clear();
                                            childImageList.clear();
                                            childClassList.clear();
                                            childListAdapter.clear();
                                            for (int i = 0; i < childArray.length(); i++) {
                                                childNameList.add(childArray.getJSONObject(i).getString("name"));
                                                childIdList.add(childArray.getJSONObject(i).getString("student_id"));
                                                childImageList.add(childArray.getJSONObject(i).getString("image"));
                                                childClassList.add(childArray.getJSONObject(i).getString("class") + " - "
                                                        + childArray.getJSONObject(i).getString("section"));
                                                childListAdapter.add(childArray.getJSONObject(i).getString("name"));
                                            }
                                            childListAdapter.notifyDataSetChanged();
                                            showChildList();
                                        }

                                        Log.e("CHILD ARRAY LENGTH", childArray.length() + "..");

                                    } else if (data.getString("role").equals("student")) {
                                        Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", true);
                                        Utility.setSharedPreference(getApplicationContext(), Constants.classSection, data.getString("class") + " (" + data.getString("section") + ")");
                                        Utility.setSharedPreference(getApplicationContext(), Constants.studentId, data.getString("student_id"));
                                        Intent asd = new Intent(getApplicationContext(), StudentDashboard.class);
                                        startActivity(asd);
                                        finish();
                                    }

                                } else {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", false);
                                }
                            } catch (JSONException e) {
                                pd.dismiss();
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e("Volley Error", error.toString());
                Toast.makeText(Login.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers1 = new HashMap<String, String>();
                headers1.put("Client-Service", "smartschool");
                headers1.put("Auth-Key", "schoolAdmin@");
                headers1.put("Content-Type", "application/json");
                Log.d("headers", "getHeaders: " + headers1.toString());
                return headers1;
            }

//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8;multipart/form-data";
//            }
//            @Override
//            public byte[] getBody() {
//                try {
//                    Log.d("body", "getBody: "+body.toString());
//                    return body == null ? null : body.toString().getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", formData, "utf-8");
//                    return null;
//                }
//            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }*/

  /*  private void postData() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utility.getSharedPreferences(getApplicationContext(), "apiUrl"))
                // .client(getHeaders())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface retrofitAPI = retrofit.create(apiInterface.class);

        // passing data from our text fields to our modal class.
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", et_userName.getText().toString());
            jsonObject.put("password", et_password.getText().toString());
            jsonObject.put("deviceToken", device_token);
            System.out.println("Login Details==" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Call<RequestBody> call = retrofitAPI.loginData(requestBody);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, retrofit2.Response<RequestBody> response) {
                try {
                    Log.d("response", "onResponse: " + response.toString());
                    Toast.makeText(Login.this, "Data added to API", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("response", "onResponse: " + e.toString());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.d("response", "onResponse: " + t.toString());
                Toast.makeText(Login.this, t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }*/

 /*   private void getDataFromApi(String bodyParams) {

        Log.d("TAG000000", "getDataFromApi: " + bodyParams.toString());

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.loginUrl;
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);
                        String success = object.getString("status");
                        String message = object.getString("message");
                        //  Utility.setSharedPreference(getApplicationContext(), Constants.loginType, object.getString("role"));
                        if (success.equals("1")) {
                            Utility.setSharedPreference(getApplicationContext(), Constants.loginType, object.getString("role"));
                            JSONObject data = object.getJSONObject("record");

                            Utility.setSharedPreference(getApplicationContext(), Constants.userId, object.getString("id"));
                            Utility.setSharedPreference(getApplicationContext(), "accessToken", object.getString("token"));

                            Utility.setSharedPreference(getApplicationContext(), "schoolName", data.getString("sch_name"));
                            Utility.setSharedPreference(getApplicationContext(), Constants.currency, data.getString("currency_symbol"));
                            Utility.setSharedPreference(getApplicationContext(), "startWeek", data.getString("start_week"));

                            String dateFormat = data.getString("date_format");
                            dateFormat = dateFormat.replace("m", "MM");
                            dateFormat = dateFormat.replace("d", "dd");
                            dateFormat = dateFormat.replace("Y", "yyyy");
                            System.out.println("dateFormat===" + dateFormat);
                            Utility.setSharedPreference(getApplicationContext(), "dateFormat", dateFormat);

                            String datesFormat = data.getString("date_format");
                            datesFormat = datesFormat.replace("m", "MM");
                            datesFormat = datesFormat.replace("d", "dd");
                            datesFormat = datesFormat.replace("Y", "yyyy");
                            String datetimeFormat = datesFormat + " " + "hh:mm aa";
                            System.out.println("datetimeFormat===" + datetimeFormat);
                            Utility.setSharedPreference(getApplicationContext(), "datetimeFormat", datetimeFormat);

                            Utility.setSharedPreference(getApplicationContext(), "language", data.getString("language"));
                            String imgUrl = Utility.getSharedPreferences(getApplicationContext(), "imagesUrl") + data.getString("image");
                            Utility.setSharedPreference(getApplicationContext(), Constants.userImage, imgUrl);
                            Utility.setSharedPreference(getApplicationContext(), Constants.userName, data.getString("username"));
                            Utility.setSharedPreference(getApplicationContext(), "schoolName", data.getString("sch_name"));

                            if (data.getString("role").equals("parent")) {
                                JSONArray childArray = data.getJSONArray("parent_childs");
                                if (childArray.length() == 1) {
                                    Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", true);
                                    Utility.setSharedPreferenceBoolean(getApplicationContext(), "hasMultipleChild", false);
                                    Utility.setSharedPreference(getApplicationContext(), Constants.studentId, childArray.getJSONObject(0).getString("student_id"));
                                    Utility.setSharedPreference(getApplicationContext(), Constants.classSection, childArray.getJSONObject(0).getString("class") + " - " + childArray.getJSONObject(0).getString("section"));
                                    Utility.setSharedPreference(getApplicationContext(), "studentName", childArray.getJSONObject(0).getString("name"));
                                    Intent asd = new Intent(getApplicationContext(), StudentDashboard.class);
                                    startActivity(asd);
                                    finish();
                                } else {

                                    Utility.setSharedPreferenceBoolean(getApplicationContext(), "hasMultipleChild", true);
                                    childNameList.clear();
                                    childIdList.clear();
                                    childImageList.clear();
                                    childClassList.clear();
                                    childListAdapter.clear();
                                    for (int i = 0; i < childArray.length(); i++) {
                                        childNameList.add(childArray.getJSONObject(i).getString("name"));
                                        childIdList.add(childArray.getJSONObject(i).getString("student_id"));
                                        childImageList.add(childArray.getJSONObject(i).getString("image"));
                                        childClassList.add(childArray.getJSONObject(i).getString("class") + " - "
                                                + childArray.getJSONObject(i).getString("section"));
                                        childListAdapter.add(childArray.getJSONObject(i).getString("name"));
                                    }
                                    childListAdapter.notifyDataSetChanged();
                                    showChildList();
                                }

                                Log.e("CHILD ARRAY LENGTH", childArray.length() + "..");

                            } else if (data.getString("role").equals("student")) {

                                Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", true);
                                Utility.setSharedPreference(getApplicationContext(), Constants.classSection, data.getString("class") + " (" + data.getString("section") + ")");
                                Utility.setSharedPreference(getApplicationContext(), Constants.studentId, data.getString("student_id"));
                                Intent asd = new Intent(getApplicationContext(), StudentDashboard.class);
                                startActivity(asd);
                                finish();

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(Login.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", "smartschool");
                headers.put("Auth-Key", "schoolAdmin@");
                headers.put("Content-Type", "application/json");
                Log.d("headers", "getHeaders: " + headers.toString());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }*/

}
