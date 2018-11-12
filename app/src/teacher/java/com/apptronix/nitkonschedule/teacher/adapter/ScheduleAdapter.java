package com.apptronix.nitkonschedule.teacher.adapter;

import android.content.Context;
import android.database.Cursor;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.teacher.data.DBContract;
import com.apptronix.nitkonschedule.ui.TimeText;

/**
 * Created by Maha Perriyava on 9/21/2017.
 */

public class ScheduleAdapter extends CursorAdapter {

    public ScheduleAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.schedule_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if(view!=null){

            ViewHolder holder = (ViewHolder) view.getTag();

            int time = cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_TIME));
            if(time-1200>0){
                holder.timeText.setTime(String.valueOf(time-1200).substring(0,1));
                holder.timeText.setAmPM("PM");
            } else {
                holder.timeText.setTime(String.valueOf(time).substring(0,1));
                holder.timeText.setAmPM("AM");
            }
            holder.headingText.setText(cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_COURSE)));
            holder.descText.setText(cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_DESCRIPTION)));

        }


    }

    public static class ViewHolder {

        TimeText timeText;
        TextView headingText, descText;

        public ViewHolder(View v){

            timeText = (TimeText) v.findViewById(R.id.scheduleItemTime);
            headingText = (TextView) v.findViewById(R.id.scheduleItemHeading);
            descText = (TextView) v.findViewById(R.id.scheduleItemDesc);

        }
    }

}
