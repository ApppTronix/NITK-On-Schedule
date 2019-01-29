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
import com.github.lzyzsd.circleprogress.CircleProgress;

import timber.log.Timber;

/**
 * Created by DevOpsTrends on 6/29/2017.
 */

public class TestsAdapter extends CursorAdapter{

    public TestsAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.tests_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor.getCount()>0){
            TestViewHolder holder = new TestViewHolder(view);
            holder.testTitle.setText(cursor.getString(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_TITLE)));
            holder.testPortions.setText(cursor.getString(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_SYLLABUS)));
            holder.testDate.setText(Utils.convertToDate(cursor.getInt(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_TEST_DATE))));
            holder.testCourse.setText(cursor.getString(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_COURSE_CODE)));
            holder.testWeightage.setProgress(cursor.getInt(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_WEIGHTAGE)));
        } else {
            Timber.i("test cursor  null");
        }
    }

    private class TestViewHolder {

        TextView testTitle, testPortions, testDate, testCourse;
        CircleProgress testWeightage;
        public TestViewHolder(View itemView) {
            testTitle=(TextView)itemView.findViewById(R.id.testTitle);
            testCourse=(TextView)itemView.findViewById(R.id.testCourse);
            testDate=(TextView)itemView.findViewById(R.id.testDate);
            testWeightage=(CircleProgress)itemView.findViewById(R.id.testWeightage);
            testPortions=(TextView)itemView.findViewById(R.id.testPortions);
        }
    }
}
