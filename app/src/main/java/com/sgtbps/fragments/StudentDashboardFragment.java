package com.sgtbps.fragments;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import java.util.Map;

public class StudentDashboardFragment extends Fragment {

    TextView attendanceValue, homeworkValue, pendingTaskValue;
    LinearLayout attendanceCard, homeworkCard, pendingTaskCard, timeTable, lesson, examination, fees, syllabus;
    ArrayList<String> moduleCodeList = new ArrayList<String>();
    ArrayList<String> moduleStatusList = new ArrayList<String>();
    public Map<String, String> headers = new HashMap<String, String>();
    public Map<String, String> params = new HashMap<String, String>();

    public StudentDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.student_dashboard_fragment, container, false);

        // Initialize UI components
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

        // Set up onClickListeners for cards
        setCardOnClickListeners();

        // Load data asynchronously
        loadData();

        return mainView;
    }

    private void setCardOnClickListeners() {
        attendanceCard.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentAttendance.class);
            getActivity().startActivity(asd);
        });

        homeworkCard.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentHomework.class);
            getActivity().startActivity(asd);
        });

        pendingTaskCard.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentTasks.class);
            getActivity().startActivity(asd);
        });

        timeTable.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentClassTimetable.class);
            getActivity().startActivity(asd);
        });

        lesson.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentSyllabusTimetable.class);
            getActivity().startActivity(asd);
        });

        examination.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentExaminationList.class);
            getActivity().startActivity(asd);
        });

        fees.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentFees.class);
            getActivity().startActivity(asd);
        });

        syllabus.setOnClickListener(view -> {
            Intent asd = new Intent(getActivity().getApplicationContext(), StudentSyllabusStatus.class);
            getActivity().startActivity(asd);
        });
    }

    private void loadData() {
        if (Utility.isConnectingToInternet(getActivity())) {
            // Prepare parameters for the API request
            params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.studentId));
            params.put("date_from", getDateOfMonth(new Date(), "first"));
            params.put("date_to", getDateOfMonth(new Date(), "last"));
            params.put("role", Utility.getSharedPreferences(getActivity(), Constants.loginType));
            params.put("user_id", Utility.getSharedPreferences(getActivity(), Constants.userId));

            JSONObject obj = new JSONObject(params);
            Log.e("params", obj.toString());

            // Make the network request
            getDataFromApi(obj.toString());
        } else {
            makeText(getActivity(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }

        // Handle module visibility logic
        handleModuleVisibility();
    }

    private void handleModuleVisibility() {
        try {
            JSONArray modulesArray = new JSONArray(Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.modulesArray));
            for (int i = 0; i < modulesArray.length(); i++) {
                JSONObject module = modulesArray.getJSONObject(i);
                String shortCode = module.getString("short_code");
                String isActive = module.getString("is_active");

                if (shortCode.equals("student_attendance") && isActive.equals("0")) {
                    attendanceCard.setVisibility(View.GONE);
                }
                if (shortCode.equals("homework") && isActive.equals("0")) {
                    homeworkCard.setVisibility(View.GONE);
                }
                if (shortCode.equals("calendar_to_do_list") && isActive.equals("0")) {
                    pendingTaskCard.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            Log.e("Error", e.toString());
        }
    }

    private void getDataFromApi(String bodyParams) {
        Log.e("RESULT PARAMS", bodyParams);
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getActivity().getApplicationContext(), "apiUrl") + Constants.getDashboardUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            pd.dismiss();
            if (response != null) {
                try {
                    Log.e("Result", response);
                    JSONObject object = new JSONObject(response);
                    String success = object.getString("success");

                    if (success.equals("1")) {
                        // Update the UI based on the API response
                        updateDashboardValues(object);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            pd.dismiss();
            Log.e("Volley Error", error.toString());
            Toast.makeText(getActivity(), R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
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
                    return bodyParams == null ? null : bodyParams.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Error", "Unsupported Encoding", e);
                    return null;
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void updateDashboardValues(JSONObject object) throws JSONException {
        if (object.getString("attendence_type").equals("0")) {
            attendanceValue.setText(object.getString("student_attendence_percentage") + "%");
        } else {
            attendanceCard.setVisibility(View.GONE);
        }

        homeworkValue.setText(object.getString("student_homework_incomplete"));
        pendingTaskValue.setText(object.getString("student_incomplete_task"));

        Utility.setSharedPreference(getActivity().getApplicationContext(), Constants.classId, object.getString("class_id"));
        Utility.setSharedPreference(getActivity().getApplicationContext(), Constants.sectionId, object.getString("section_id"));
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
