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
 * Created by DevOpsTrends on 6/25/2017.
 */

public class AssignmentsAdapter extends CursorAdapter{

    public AssignmentsAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.assignment_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor.getCount()>0){
            AssignmentViewHolder holder = new AssignmentViewHolder(view);
            holder.assignmentTitle.setText(cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_TITLE)));
            holder.assignmentDescription.setText(cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_DESCRIPTION)));
            holder.assignmentSubmissionDate.setText(Utils.convertToDate(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_SUBMISSION_DATE)))));
            holder.assignmentCourse.setText(cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_COURSE_CODE)));
            holder.assignmentWeightage.setProgress(cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_WEIGHTAGE)));
        } else {
            Timber.i("assignment cursor  null");
        }
    }

    private class AssignmentViewHolder {

        TextView assignmentTitle, assignmentDescription, assignmentSubmissionDate, assignmentCourse;
        CircleProgress assignmentWeightage;
        public AssignmentViewHolder(View itemView) {
            assignmentTitle=(TextView)itemView.findViewById(R.id.assignmentTitle);
            assignmentCourse=(TextView)itemView.findViewById(R.id.assignmentCourse);
            assignmentSubmissionDate=(TextView)itemView.findViewById(R.id.assignmentSubmission);
            assignmentWeightage=(CircleProgress)itemView.findViewById(R.id.assignmentWeightage);
            assignmentDescription=(TextView)itemView.findViewById(R.id.assignmentDescription);
        }
    }
}
