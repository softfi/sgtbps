package com.sgtbps.students;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sgtbps.BaseActivity;
import com.sgtbps.R;
import com.sgtbps.adapters.StudentHomeworkAdapter;
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

public class StudentHomework extends BaseActivity {
    RecyclerView homeworkListView;
    ArrayList<String> homeworkIdList = new ArrayList<String>();
    ArrayList<String> homeworkTitleList = new ArrayList<String>();
    ArrayList<String> homeworkSubjectNameList = new ArrayList<String>();
    ArrayList<String> homeworkHomeworkDateList = new ArrayList<String>();
    ArrayList<String> homeworkSubmissionDateList = new ArrayList<String>();
    ArrayList<String> homeworkEvaluationDateList = new ArrayList<String>();
    ArrayList<String> homeworkEvaluationByList = new ArrayList<String>();
    ArrayList<String> homeworkCreatedByList = new ArrayList<String>();
    ArrayList<String> homeworkDocumentList = new ArrayList<String>();
    ArrayList<String> homeworkClassList = new ArrayList<String>();
    ArrayList<String> homeworkStatusList = new ArrayList<String>();
    ArrayList<String> subStatus = new ArrayList<String>();
    StudentHomeworkAdapter adapter;
    LinearLayout nodata_layout;
    SwipeRefreshLayout pullToRefresh;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_homework_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.homework));
        homeworkListView = (RecyclerView) findViewById(R.id.studentHostel_listview);
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        adapter = new StudentHomeworkAdapter(StudentHomework.this, homeworkIdList, homeworkTitleList, homeworkSubjectNameList,
                homeworkHomeworkDateList, homeworkSubmissionDateList, homeworkEvaluationDateList, homeworkEvaluationByList,
                homeworkCreatedByList, homeworkDocumentList, homeworkClassList, homeworkStatusList,subStatus);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        homeworkListView.setLayoutManager(mLayoutManager);
        homeworkListView.setItemAnimator(new DefaultItemAnimator());
        homeworkListView.setAdapter(adapter);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                loadData();
            }
        });
        loadData();
    }

    public void loadData() {
        if (Utility.isConnectingToInternet(StudentHomework.this)) {
            params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId));
            JSONObject obj = new JSONObject(params);
            Log.e("params ", obj.toString());
            getDataFromApi(obj.toString());

        } else {
            makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void getDataFromApi(String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.getHomeworkUrl;
        Log.d("TAG", requestBody + "getDataFromApi: " + url);
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pullToRefresh.setRefreshing(false);
                if (result != null) {
                    pd.dismiss();
                    try {

                        Log.d("TAG", "Resultszx: " + result);
                        JSONObject obj = new JSONObject(result);
                        JSONArray dataArray = obj.getJSONArray("homeworklist");
                        Log.d("TAG", "onResponse: " + dataArray);

                        homeworkIdList.clear();
                        homeworkTitleList.clear();
                        homeworkSubjectNameList.clear();
                        homeworkHomeworkDateList.clear();
                        homeworkSubmissionDateList.clear();
                        homeworkCreatedByList.clear();
                        homeworkEvaluationByList.clear();
                        homeworkDocumentList.clear();
                        homeworkClassList.clear();
                        homeworkEvaluationDateList.clear();
                        homeworkStatusList.clear();
                        subStatus.clear();

                        if (dataArray.length() != 0) {
                            nodata_layout.setVisibility(View.GONE);
                            homeworkListView.setVisibility(View.VISIBLE);

                            for (int i = 0; i < dataArray.length(); i++) {
                                Log.d("Error", "fjrhjggry" + dataArray.getJSONObject(i).getString("id"));
                                homeworkIdList.add(dataArray.getJSONObject(i).getString("id"));
                                homeworkTitleList.add(dataArray.getJSONObject(i).getString("description"));
                                homeworkSubjectNameList.add(dataArray.getJSONObject(i).getString("name"));
                                homeworkHomeworkDateList.add(dataArray.getJSONObject(i).getString("homework_date"));
                                homeworkSubmissionDateList.add(dataArray.getJSONObject(i).getString("submit_date"));
                                homeworkCreatedByList.add(dataArray.getJSONObject(i).getString("staff_created"));
                                homeworkEvaluationByList.add(dataArray.getJSONObject(i).getString("staff_evaluated"));
                                String fileName = "";

                                subStatus.add(dataArray.getJSONObject(i).getString("status"));
                                if (!dataArray.getJSONObject(i).getString("document").equals("null") && !dataArray.getJSONObject(i).getString("document").isEmpty()) {
                                    String filePath = dataArray.getJSONObject(i).getString("document");
                                    String extension = filePath.substring(filePath.lastIndexOf("."));
                                    fileName = dataArray.getJSONObject(i).getString("id") + extension;

                                }
                                homeworkDocumentList.add(fileName);
                                homeworkClassList.add(dataArray.getJSONObject(i).getString("class") + " " + dataArray.getJSONObject(i).getString("section"));

                                if (dataArray.getJSONObject(i).getString("evaluation_date").equals("0000-00-00")) {
                                    homeworkEvaluationDateList.add("");
                                } else {
                                    homeworkEvaluationDateList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("evaluation_date")));
                                }
                                homeworkStatusList.add(dataArray.getJSONObject(i).getString("homework_evaluation_id"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            nodata_layout.setVisibility(View.VISIBLE);
                            homeworkListView.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    pullToRefresh.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentHomework.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
                //Log.e("Headers", headers.toString());
                 Log.d("TAG", "getHeaders: "+headers);
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentHomework.this); //Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }
}


