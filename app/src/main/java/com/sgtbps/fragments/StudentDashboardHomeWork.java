package com.sgtbps.fragments;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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
import com.sgtbps.R;
import com.sgtbps.adapters.StudentHomeworkAdapter;
import com.sgtbps.students.StudentHomework;
import com.sgtbps.utils.Constants;
import com.sgtbps.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StudentDashboardHomeWork extends Fragment {

    RecyclerView homeworkListView;
    ArrayList<String> homeworkIdList = new ArrayList<>();
    ArrayList<String> homeworkTitleList = new ArrayList<>();
    ArrayList<String> homeworkSubjectNameList = new ArrayList<>();
    ArrayList<String> homeworkHomeworkDateList = new ArrayList<>();
    ArrayList<String> homeworkSubmissionDateList = new ArrayList<>();
    ArrayList<String> homeworkEvaluationDateList = new ArrayList<>();
    ArrayList<String> homeworkEvaluationByList = new ArrayList<>();
    ArrayList<String> homeworkCreatedByList = new ArrayList<>();
    ArrayList<String> homeworkDocumentList = new ArrayList<>();
    ArrayList<String> homeworkClassList = new ArrayList<>();
    ArrayList<String> homeworkStatusList = new ArrayList<>();
    ArrayList<String> subStatus = new ArrayList<>();
    public String defaultDateFormat, currency;
    SwipeRefreshLayout pullToRefresh;
    StudentHomeworkAdapter adapter;
    LinearLayout nodata_layout;
    public Map<String, String> params = new HashMap<>();
    public Map<String, String> headers = new HashMap<>();

    public StudentDashboardHomeWork() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize data
        defaultDateFormat = Utility.getSharedPreferences(getActivity(), "dateFormat");
        currency = Utility.getSharedPreferences(getActivity(), Constants.currency);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View mainView = inflater.inflate(R.layout.student_homework_activity, container, false);

        homeworkListView = mainView.findViewById(R.id.studentHostel_listview);
        nodata_layout = mainView.findViewById(R.id.nodata_layout);
        pullToRefresh = mainView.findViewById(R.id.pullToRefresh);

        // Initialize adapter
        adapter = new StudentHomeworkAdapter(getActivity(), homeworkIdList, homeworkTitleList, homeworkSubjectNameList,
                homeworkHomeworkDateList, homeworkSubmissionDateList, homeworkEvaluationDateList, homeworkEvaluationByList,
                homeworkCreatedByList, homeworkDocumentList, homeworkClassList, homeworkStatusList, subStatus);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        homeworkListView.setLayoutManager(mLayoutManager);
        homeworkListView.setItemAnimator(new DefaultItemAnimator());
        homeworkListView.setAdapter(adapter);

        // Swipe-to-refresh functionality
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        // Load data when view is created
        loadData();
        return mainView;
    }

    private void loadData() {
        if (Utility.isConnectingToInternet(getActivity())) {
            params.put("student_id", Utility.getSharedPreferences(getActivity(), Constants.studentId));
            JSONObject obj = new JSONObject(params);
            Log.e("params", obj.toString());
            getDataFromApi(obj.toString());
        } else {
            makeText(getActivity(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromApi(String rawJsonData) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getActivity(), "apiUrl") + Constants.getHomeworkUrl;
        Log.d("TAG", "Raw JSON Data: " + rawJsonData + " URL: " + url);
        Log.e("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, result -> {
            pullToRefresh.setRefreshing(false);
            if (result != null) {
                pd.dismiss();
                try {
                    Log.d("TAG", "Results: " + result);
                    JSONObject obj = new JSONObject(result);
                    JSONArray dataArray = obj.getJSONArray("homeworklist");
                    Log.d("TAG", "onResponse: " + dataArray);

                    // Clear existing lists to load fresh data
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
                            homeworkIdList.add(dataArray.getJSONObject(i).getString("id"));
                            homeworkTitleList.add(dataArray.getJSONObject(i).getString("description"));
                            homeworkSubjectNameList.add(dataArray.getJSONObject(i).getString("name"));
                            homeworkHomeworkDateList.add(dataArray.getJSONObject(i).getString("homework_date"));
                            homeworkSubmissionDateList.add(dataArray.getJSONObject(i).getString("submit_date"));
                            homeworkCreatedByList.add(dataArray.getJSONObject(i).getString("staff_created"));
                            homeworkEvaluationByList.add(dataArray.getJSONObject(i).getString("staff_evaluated"));
                            subStatus.add(dataArray.getJSONObject(i).getString("status"));

                            String fileName = "";
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(Objects.requireNonNull(getContext()), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getContext(), "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";  // We specify that we're sending raw JSON data
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {

                    return rawJsonData == null ? null : rawJsonData.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", rawJsonData, "utf-8");
                    return null;
                }
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        requestQueue.add(stringRequest);
    }

}
