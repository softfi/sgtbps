package com.sgtbps.fragments;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
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

import com.sgtbps.R;
import com.sgtbps.students.StudentAttendance;
import com.sgtbps.students.StudentClassTimetable;
import com.sgtbps.students.StudentExaminationList;
import com.sgtbps.students.StudentFees;
import com.sgtbps.students.StudentHomework;
import com.sgtbps.students.StudentSyllabusStatus;
import com.sgtbps.students.StudentSyllabusTimetable;
import com.sgtbps.students.StudentTasks;
import com.sgtbps.utils.Constants;
import com.sgtbps.utils.Utility;


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

public class StudentDashboardFragment extends Fragment {

    TextView attendanceValue, homeworkValue, pendingTaskValue;
    LinearLayout attendanceCard,homeworkCard,pendingTaskCard,timeTable,lesson,examination,fees,syllabus;
    //FrameLayout calenderFrame;
    GridView gridView;
    ArrayList<String> moduleCodeList = new ArrayList<String>();
    ArrayList<String> moduleStatusList = new ArrayList<String>();
    public Map<String, String> headers = new HashMap<String, String>();
    public Map<String, String> params = new Hashtable<String, String>();
    JSONArray modulesJson;
    public StudentDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      // loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.student_dashboard_fragment, container, false);
        //calenderFrame = mainView.findViewById(R.id.dashboardViewPager);




        attendanceCard = mainView.findViewById(R.id.card1);
        homeworkCard = mainView.findViewById(R.id.card2);
        pendingTaskCard = mainView.findViewById(R.id.card3);

        timeTable = mainView.findViewById(R.id.card5);
        lesson = mainView.findViewById(R.id.card6);
        examination = mainView.findViewById(R.id.card4);
        fees = mainView.findViewById(R.id.card7);
        syllabus = mainView.findViewById(R.id.card8);

        attendanceValue = mainView.findViewById(R.id.student_dashboard_fragment_attendance_value);
        homeworkValue = mainView.findViewById(R.id.student_dashboard_fragment_homework_value);
        pendingTaskValue = mainView.findViewById(R.id.student_dashboard_fragment_pendingTask_value);



        loadData();


        attendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentAttendance.class);
                getActivity().startActivity(asd);
            }
        });

        homeworkCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentHomework.class);
                getActivity().startActivity(asd);
            }
        });

        pendingTaskCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentTasks.class);
                getActivity().startActivity(asd);
            }
        });

        timeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentClassTimetable.class);
                getActivity().startActivity(asd);
            }
        });
        lesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentSyllabusTimetable.class);
                getActivity().startActivity(asd);
            }
        });
        examination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentExaminationList.class);
                getActivity().startActivity(asd);
            }
        });

        fees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentFees.class);
                getActivity().startActivity(asd);
            }
        });
        syllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(getActivity().getApplicationContext(), StudentSyllabusStatus.class);
                getActivity().startActivity(asd);
            }
        });





        Log.e("STATUS", "onCreateView");
        return mainView;
    }


    private void loadData() {
       // decorate();
       // loadFragment(new DashboardCalender());

        if(Utility.getSharedPreferences(getActivity(), Constants.loginType).equals("parent")){
            if (Utility.isConnectingToInternet(getActivity())) {
                params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.studentId));
                params.put("date_from", getDateOfMonth(new Date(), "first"));
                params.put("date_to", getDateOfMonth(new Date(), "last"));
                params.put("role", Utility.getSharedPreferences(getActivity(), Constants.loginType));
                params.put("user_id", Utility.getSharedPreferences(getActivity(), Constants.userId));
                JSONObject obj=new JSONObject(params);
                Log.e("params ", obj.toString());
                getDataFromApi(obj.toString());
            } else {
                makeText(getActivity(),R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
            }

        }else{
            if (Utility.isConnectingToInternet(getActivity())) {
                params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.studentId));
                params.put("date_from", getDateOfMonth(new Date(), "first"));
                params.put("date_to", getDateOfMonth(new Date(), "last"));
                params.put("role", Utility.getSharedPreferences(getActivity(), Constants.loginType));
                JSONObject obj=new JSONObject(params);
                Log.e("params ", obj.toString());
                getDataFromApi(obj.toString());
            } else {
                makeText(getActivity(),R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
            }

        }

        try {
            JSONArray modulesArray = new JSONArray(Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.modulesArray));

            if(modulesArray.length() != 0) {
                ArrayList<String> moduleCodeList = new ArrayList<String>();
                ArrayList<String> moduleStatusList = new ArrayList<String>();

                for (int i = 0; i<modulesArray.length(); i++) {
                    if(modulesArray.getJSONObject(i).getString("short_code").equals("student_attendance")
                        && modulesArray.getJSONObject(i).getString("is_active").equals("0") ) {
                        attendanceCard.setVisibility(View.GONE);
                    } if(modulesArray.getJSONObject(i).getString("short_code").equals("homework")
                            && modulesArray.getJSONObject(i).getString("is_active").equals("0") ) {
                        homeworkCard.setVisibility(View.GONE);
                    } if(modulesArray.getJSONObject(i).getString("short_code").equals("calendar_to_do_list")
                            && modulesArray.getJSONObject(i).getString("is_active").equals("0") ) {
                        pendingTaskCard.setVisibility(View.GONE);
                       // calenderFrame.setVisibility(View.GONE);
                    }
                }
            }
        } catch (JSONException e) {
            Log.d("Error", e.toString());
        }
    }


    private void getDataFromApi (String bodyParams) {

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
                            if(object.getString("attendence_type").equals("0")){
                                attendanceValue.setText(object.getString("student_attendence_percentage") + "%");
                            }else{
                                attendanceCard.setVisibility(View.GONE);
                            }
                            homeworkValue.setText(object.getString("student_homework_incomplete"));
                            pendingTaskValue.setText(object.getString("student_incomplete_task"));

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

//    private void decorate() {
//        attendanceLayout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.secondaryColour)));
//        homeworkLayout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.secondaryColour)));
//        pendingTaskLayout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.secondaryColour)));
//    }

/*    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(calenderFrame.getId(), fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }*/

    public static String getDateOfMonth(Date date, String index){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(index.equals("first")) {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        } else {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(cal.getTime());
    }
}
