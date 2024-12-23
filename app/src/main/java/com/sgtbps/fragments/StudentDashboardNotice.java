package com.sgtbps.fragments;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sgtbps.R;
import com.sgtbps.adapters.StudentNotificationAdapter;
import com.sgtbps.utils.Constants;
import com.sgtbps.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentDashboardNotice extends Fragment {

    ListView notificationList;
    StudentNotificationAdapter adapter;
    public String defaultDateFormat, currency;
    ArrayList<String> noticeTitleId = new ArrayList<String>();
    ArrayList<String> noticeTitleList = new ArrayList<String>();
    ArrayList <String> noticeDateList = new ArrayList<String>();
    ArrayList <String> noticeDescList = new ArrayList<String>();
    LinearLayout nodata_layout;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    public StudentDashboardNotice() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadData() {
        adapter = new StudentNotificationAdapter(getActivity(), noticeTitleId, noticeTitleList, noticeDateList, noticeDescList);
        notificationList.setAdapter(adapter);
        if(Utility.isConnectingToInternet(getActivity())){
            params.put("type", Utility.getSharedPreferences(getContext(), Constants.loginType));
            JSONObject obj=new JSONObject(params);
            Log.e("params ", obj.toString());
            getDataFromApi(obj.toString());
        }else{
            makeText(getActivity(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.student_notice_board_activity, container, false);
        notificationList = (ListView) mainView.findViewById(R.id.studentNotice_listview);
        nodata_layout = (LinearLayout) mainView.findViewById(R.id.nodata_layout);
        loadData();
        defaultDateFormat = Utility.getSharedPreferences(getActivity(), "dateFormat");
        currency = Utility.getSharedPreferences(getActivity(), Constants.currency);
        return mainView;
    }

    private void getDataFromApi(String rawRequestBody) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getActivity(), "apiUrl") + Constants.getNotificationsUrl;

        Log.d("TAG", "Request Body: " + rawRequestBody + " URL: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    Log.d("TAG", "Response: " + result);
                    try {
                        JSONObject object = new JSONObject(result);

                        String success = object.getString("success");
                        if (success.equals("1")) {
                            notificationList.setVisibility(View.VISIBLE);
                            nodata_layout.setVisibility(View.GONE);
                            JSONArray dataArray = object.getJSONArray("data");
                            Log.e("length", dataArray.length() + "..");

                            for (int i = 0; i < dataArray.length(); i++) {
                                noticeTitleId.add(dataArray.getJSONObject(i).getString("id"));
                                noticeTitleList.add(dataArray.getJSONObject(i).getString("title"));
                                noticeDateList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("date")));
                                noticeDescList.add(dataArray.getJSONObject(i).getString("message"));
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            notificationList.setVisibility(View.GONE);
                            nodata_layout.setVisibility(View.VISIBLE);
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
                Toast.makeText(getActivity(), R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getActivity(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getActivity(), "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return rawRequestBody == null ? null : rawRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", rawRequestBody, "utf-8");
                    return null;
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}
