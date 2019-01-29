package com.apptronix.nitkonschedule.teacher.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.Utils;
import com.apptronix.nitkonschedule.teacher.data.DBContract;

import timber.log.Timber;

/**
 * Created by DevOpsTrends on 6/29/2017.
 */

public class AttendanceAdapter extends CursorAdapter {
    public AttendanceAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.attendance_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Timber.i("view binded");
        TextView date_time, course;
        date_time=view.findViewById(R.id.date_time);
        course=view.findViewById(R.id.course);
        String course_s;
        int date_s, time_s;
        course_s = cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_COURSE));
        date_s = cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_DATE));
        time_s = cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_TIME));
        date_time.setText(Utils.convertToDate(date_s)+" "+Utils.timeFromInt(time_s));
        course.setText(course_s);
    }
}
