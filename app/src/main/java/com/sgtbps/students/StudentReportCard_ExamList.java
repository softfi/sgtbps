package com.sgtbps.students;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.sgtbps.adapters.StudentReportCard_ExamListAdapter;
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

public class StudentReportCard_ExamList extends BaseActivity {

    RecyclerView examListview;
    ArrayList<String> examIdList = new ArrayList<String>();
    ArrayList<String> examNameList = new ArrayList<String>();
    ArrayList<String> totalList = new ArrayList<String>();
    ArrayList<String> percentList = new ArrayList<String>();
    ArrayList<String> gradeList = new ArrayList<String>();
    ArrayList<String> statusList = new ArrayList<String>();
    StudentReportCard_ExamListAdapter adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_report_card_exam_list_activity, null, false);
        mDrawerLayout.addView(contentView, 0);
        titleTV.setText(getApplicationContext().getString(R.string.reportCard));

        examListview = (RecyclerView) findViewById(R.id.studentReportCard_examListview);

        adapter = new StudentReportCard_ExamListAdapter(getApplicationContext(), examIdList, examNameList, totalList, percentList, gradeList, statusList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        examListview.setLayoutManager(mLayoutManager);
        examListview.setItemAnimator(new DefaultItemAnimator());
        examListview.setAdapter(adapter);
        if (Utility.isConnectingToInternet(getApplicationContext())) {
            params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), "studentId"));
            JSONObject obj = new JSONObject(params);
            Log.e("params ", obj.toString());
            getDataFromApi(obj.toString());
        } else {
            makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }

    }

    private void getDataFromApi(String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.getExamResultListUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);

                        String success = object.getString("status");
                        if (success.equals("200")) {

                            JSONArray dataArray = object.getJSONArray("examList");
                            for (int i = 0; i < dataArray.length(); i++) {

                                examNameList.add(dataArray.getJSONObject(i).getString("exam_name"));
                                JSONObject examResult = dataArray.getJSONObject(i).getJSONObject("exam_result");
                                examIdList.add(examResult.getString("exam_id"));
                                totalList.add(examResult.getString("get_marks") + "/" + examResult.getString("total_marks"));
                                percentList.add(examResult.getString("percentage"));
                                gradeList.add(examResult.getString("grade"));
                                statusList.add(examResult.getString("result"));

                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
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
                Log.d("userID", "onErrorResponse: " + Utility.getSharedPreferences(getApplicationContext(), "userId"));
                Log.d("accessToken", "onErrorResponse: " + Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentReportCard_ExamList.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
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
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(StudentReportCard_ExamList.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
