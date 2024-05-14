package com.sgtbps.fragments;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.sgtbps.CourseModel;
import com.sgtbps.R;
import com.sgtbps.students.StudentAttendance;
import com.sgtbps.students.StudentHomework;
import com.sgtbps.students.StudentTasks;
import com.sgtbps.utils.Constants;
import com.sgtbps.utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class DashboardFragment extends Fragment {

    GridView homeGrid;

    LinearLayout attendanceLayout, homeworkLayout, pendingLayout;
    TextView nameText, attendanceValue, pendingValue, homeworkValue;
    ImageView profileImage;
    public Map<String, String> headers = new HashMap<String, String>();
    public Map<String, String> params = new Hashtable<String, String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        initView(view);
        return view;


    }

    private void initView(View view) {
        homeGrid = view.findViewById(R.id.my_grid);
        homeworkLayout = view.findViewById(R.id.homework_layout);
        attendanceLayout = view.findViewById(R.id.attendance_layout);
        pendingLayout = view.findViewById(R.id.pending_layout);
        nameText = view.findViewById(R.id.name);
        profileImage = view.findViewById(R.id.dash_image);
        attendanceValue = view.findViewById(R.id.total_attendance);
        homeworkValue = view.findViewById(R.id.homework_value);
        pendingValue = view.findViewById(R.id.pendin_value);
        nameText.setText("Hi, "+Utility.getSharedPreferences(getContext(), Constants.userName));


        Picasso.with(getContext()).load(Utility.getSharedPreferences(getContext(), "userImage")).placeholder(R.drawable.user).into(profileImage);

        attendanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentAttendance.class);
                getActivity().startActivity(asd);
            }
        });

        homeworkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentHomework.class);
                getActivity().startActivity(asd);
            }
        });

        pendingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentTasks.class);
                getActivity().startActivity(asd);
            }
        });

        loadData();

        ArrayList<CourseModel> courseModelArrayList = new ArrayList<>();
        courseModelArrayList.add(new CourseModel("Examination", R.drawable.exam1));
        courseModelArrayList.add(new CourseModel("Time Table", R.drawable.timetb));
        courseModelArrayList.add(new CourseModel("Lesson Plan", R.drawable.lesson));
        courseModelArrayList.add(new CourseModel("Fees", R.drawable.fees22));
        courseModelArrayList.add(new CourseModel("Syllabus", R.drawable.syllabus));
        courseModelArrayList.add(new CourseModel("Download center", R.drawable.file));
        courseModelArrayList.add(new CourseModel("Notice Board", R.drawable.notice));
        courseModelArrayList.add(new CourseModel("Online Exam", R.drawable.online_exam));
        courseModelArrayList.add(new CourseModel("Library", R.drawable.library));

        com.sgtbps.adapters.HomeDashBoardAdapter adapter = new com.sgtbps.adapters.HomeDashBoardAdapter(getContext(), courseModelArrayList);
        // homeGrid.setVerticalScrollBarEnabled(false);
        homeGrid.setAdapter(adapter);
    }


    private void loadData() {
        // decorate();
        // loadFragment(new DashboardCalender());

        if (Utility.getSharedPreferences(getActivity(), Constants.loginType).equals("parent")) {
            if (Utility.isConnectingToInternet(getActivity())) {
                params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.studentId));
                params.put("date_from", getDateOfMonth(new Date(), "first"));
                params.put("date_to", getDateOfMonth(new Date(), "last"));
                params.put("role", Utility.getSharedPreferences(getActivity(), Constants.loginType));
                params.put("user_id", Utility.getSharedPreferences(getActivity(), Constants.userId));
                JSONObject obj = new JSONObject(params);
                Log.e("params958678934576 ", obj.toString());
                getDataFromApi(obj.toString());
            } else {
                makeText(getActivity(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
            }

        } else {
            if (Utility.isConnectingToInternet(getActivity())) {
                params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.studentId));
                params.put("date_from", getDateOfMonth(new Date(), "first"));
                params.put("date_to", getDateOfMonth(new Date(), "last"));
                params.put("role", Utility.getSharedPreferences(getActivity(), Constants.loginType));
                JSONObject obj = new JSONObject(params);
                Log.e("params4356", obj.toString());
                getDataFromApi(obj.toString());
            } else {
                makeText(getActivity(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
            }

        }

        try {
            JSONArray modulesArray = new JSONArray(Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.modulesArray));

            if (modulesArray.length() != 0) {

               /* ArrayList<String> moduleCodeList = new ArrayList<String>();
                ArrayList<String> moduleStatusList = new ArrayList<String>();*/

                for (int i = 0; i < modulesArray.length(); i++) {
                    if (modulesArray.getJSONObject(i).getString("short_code").equals("student_attendance")
                            && modulesArray.getJSONObject(i).getString("is_active").equals("0")) {
                        attendanceLayout.setVisibility(View.GONE);
                    }
                    if (modulesArray.getJSONObject(i).getString("short_code").equals("homework")
                            && modulesArray.getJSONObject(i).getString("is_active").equals("0")) {
                        homeworkLayout.setVisibility(View.GONE);
                    }
                    if (modulesArray.getJSONObject(i).getString("short_code").equals("calendar_to_do_list")
                            && modulesArray.getJSONObject(i).getString("is_active").equals("0")) {
                        pendingLayout.setVisibility(View.GONE);
                        // calenderFrame.setVisibility(View.GONE);
                    }
                }
            }
        } catch (JSONException e) {
            Log.d("Error", e.toString());
        }
    }


    private void getDataFromApi(String bodyParams) {

        Log.e("RESULT PARAMS", bodyParams);
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getActivity().getApplicationContext(), "apiUrl") + Constants.getDashboardUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);
                        //TODO success
                        String success = "1"; //object.getString("success");
                        if (success.equals("1")) {
                            if (object.getString("attendence_type").equals("0")) {
                                attendanceValue.setText(object.getString("student_attendence_percentage") + "%");
                            } else {
                                attendanceLayout.setVisibility(View.GONE);
                            }
                            homeworkValue.setText(object.getString("student_homework_incomplete"));
                            pendingValue.setText(object.getString("student_incomplete_task"));

                            Utility.setSharedPreference(getActivity().getApplicationContext(), Constants.classId, object.getString("class_id"));
                            Utility.setSharedPreference(getActivity().getApplicationContext(), Constants.sectionId, object.getString("section_id"));


                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
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
                headers.put("User-ID", Utility.getSharedPreferences(getActivity().getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getActivity().getApplicationContext(), "accessToken"));
               Log.e("tasdffsaf",headers.toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());//Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }

    public static String getDateOfMonth(Date date, String index) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (index.equals("first")) {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        } else {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(cal.getTime());
    }
}
