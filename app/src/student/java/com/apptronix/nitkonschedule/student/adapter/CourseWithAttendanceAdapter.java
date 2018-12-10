package com.apptronix.nitkonschedule.student.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.student.data.DBContract;

/**
 * Created by DevOpsTrends on 6/29/2017.
 */

public class CourseWithAttendanceAdapter extends CursorAdapter {
    public CourseWithAttendanceAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.course_with_attendance_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView attendancePercentage, course;
        attendancePercentage= view.findViewById(R.id.attendancePercentage);
        course= view.findViewById(R.id.attendancePercentageCourse);
        String attendance  = cursor.getString(cursor.getColumnIndex(DBContract.CourseEntry.COLUMN_ATT_PERCENT))+"%";
        attendancePercentage.setText(attendance);
        course.setText(cursor.getString(cursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE)));
    }
}
