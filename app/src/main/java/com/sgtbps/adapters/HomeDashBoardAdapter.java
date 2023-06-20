package com.sgtbps.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.sgtbps.CourseModel;
import com.sgtbps.R;
import com.sgtbps.students.StudentClassTimetable;
import com.sgtbps.students.StudentDownloads;
import com.sgtbps.students.StudentExaminationList;
import com.sgtbps.students.StudentFees;
import com.sgtbps.students.StudentLibraryBookIssued;
import com.sgtbps.students.StudentNoticeBoard;
import com.sgtbps.students.StudentOnlineExam;
import com.sgtbps.students.StudentSyllabusStatus;
import com.sgtbps.students.StudentSyllabusTimetable;

import java.util.ArrayList;

public class HomeDashBoardAdapter extends ArrayAdapter<CourseModel> {

    public HomeDashBoardAdapter(@NonNull Context context, ArrayList<CourseModel> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_layout, parent, false);
        }

        final CourseModel courseModel = getItem(position);
        TextView titleText = listitemView.findViewById(R.id.title);
        final ImageView imageView = listitemView.findViewById(R.id.img);

        LinearLayout cardView = listitemView.findViewById(R.id.my_card);

        titleText.setText(courseModel.getTitle());
        imageView.setImageResource(courseModel.getImage());

        if (position == 0) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.darkBlue));
        } else if (position == 1) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.lightGreen));
        } else if (position == 2) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.darkRed));
        } else if (position == 3) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.yellow));
        } else if (position == 4) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.lightBlue));
        } else if (position == 5) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.lightGreen));
        } else if (position == 6) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.darkRed));
        } else if (position == 7) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.darkBlue));
        } else if (position == 8) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.yellow));
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courseModel.getTitle().equals("Examination")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentExaminationList.class));
                } else if (courseModel.getTitle().equals("Time Table")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentClassTimetable.class));
                } else if (courseModel.getTitle().equals("Lesson Plan")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentSyllabusTimetable.class));
                } else if (courseModel.getTitle().equals("Fees")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentFees.class));
                } else if (courseModel.getTitle().equals("Syllabus")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentSyllabusStatus.class));
                } else if (courseModel.getTitle().equals("Download center")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentDownloads.class));
                } else if (courseModel.getTitle().equals("Notice Board")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentNoticeBoard.class));
                } else if (courseModel.getTitle().equals("Online Exam")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentOnlineExam.class));
                } else if (courseModel.getTitle().equals("Library")) {
                    v.getContext().startActivity(new Intent(getContext(), StudentLibraryBookIssued.class));
                }
            }
        });

        return listitemView;
    }
}

