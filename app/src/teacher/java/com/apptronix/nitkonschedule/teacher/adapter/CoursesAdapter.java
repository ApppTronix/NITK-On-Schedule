package com.apptronix.nitkonschedule.teacher.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.teacher.data.DBContract;

import timber.log.Timber;

/**
 * Created by Maha Perriyava on 12/20/2017.
 */

public class CoursesAdapter extends CursorAdapter {
    public CoursesAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.courses_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor.getCount()>0){
            CoursesAdapter.CourseViewHolder holder = new CoursesAdapter.CourseViewHolder(view);
            holder.courseName.setText(cursor.getString(cursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE_DESCRIPTION)));
            holder.courseCode.setText(cursor.getString(cursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE)));
        } else {
            Timber.i("courses cursor  null");
        }
    }

    private class CourseViewHolder {

        TextView courseName,courseCode;
        public CourseViewHolder(View itemView) {
            courseName=(TextView)itemView.findViewById(R.id.courseName);
            courseCode=(TextView)itemView.findViewById(R.id.courseCode);
        }
    }
}
