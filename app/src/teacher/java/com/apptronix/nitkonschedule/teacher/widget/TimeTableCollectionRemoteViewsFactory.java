package com.apptronix.nitkonschedule.teacher.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.teacher.data.DBContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by DevOpsTrends on 5/10/2017.
 */

public class TimeTableCollectionRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private Context mContext;
    private Cursor mCursor;

    public TimeTableCollectionRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;

    }

    @Override
    public void onCreate() {
        Timber.i("widget factory created");
    }


    @Override
    public void onDataSetChanged() {

        Timber.i("widget on data set changed called");
        if (mCursor != null) {
            mCursor.close();
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        Calendar calendar = Calendar.getInstance();
        String weekDay = dayFormat.format(calendar.getTime());
        double date = calendar.get(Calendar.YEAR)*10000+calendar.get(Calendar.MONTH)*100+calendar.get(Calendar.DAY_OF_MONTH);
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(DBContract.TimeTableEntry.buildDateTableUri((int)date),
                null,
                null,
                null,
                null);
        Timber.i("widget count %d",mCursor.getCount());
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.timetable_collection_widget_item);

        String time = mCursor.getString(mCursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_TIME));
        String course = mCursor.getString(mCursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_COURSE));
        if(course==null||time==null){
            return rv;
        }
        Timber.i("%s%s",time,course);
        rv.setTextViewText(R.id.widgetTime, time);
        rv.setTextViewText(R.id.widgetCourse, course);

        rv.setContentDescription(R.id.widgetItemContainer,course);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
