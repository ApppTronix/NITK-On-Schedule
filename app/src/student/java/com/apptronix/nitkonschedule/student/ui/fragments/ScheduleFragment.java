package com.apptronix.nitkonschedule.student.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.Utils;
import com.apptronix.nitkonschedule.student.adapter.ScheduleAdapter;
import com.apptronix.nitkonschedule.student.data.DBContract;
import com.apptronix.nitkonschedule.student.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;
import timber.log.Timber;


public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private OnFragmentInteractionListener mListener;

        int selectedDate,startDate,endDate,todayDate;
        ScheduleAdapter scheduleAdapter;
        Calendar c;
        HorizontalCalendar horizontalCalendar;
        Context mContext;



        @Override
        public void onAttach(Context context) {
                super.onAttach(context);
                if (context instanceof OnFragmentInteractionListener) {
                        mListener = (OnFragmentInteractionListener) context;
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                        Calendar calendar = Calendar.getInstance();
                        String weekDay = dayFormat.format(calendar.getTime());
                        if (Objects.equals(weekDay, "Saturday") || Objects.equals(weekDay, "Sunday")) {
                                weekDay = "Monday";
                        }

                        mListener.onFragmentInteraction(weekDay + "'s Time Table");
                } else {
                        throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
                }
        }

        @Override
        public void onDetach() {
                super.onDetach();

                mListener = null;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);

            todayDate=year*10000+(month)*100+day;
            Cursor maxDate = getActivity().getContentResolver().query(DBContract.TimeTableEntry.maxDate(), new String[]{DBContract.TimeTableEntry.TABLE_NAME}, "MAX(" + DBContract.TimeTableEntry.COLUMN_DATE + ")", null, null);
            if (maxDate != null) {
                if (maxDate.moveToFirst()) {
                    endDate = maxDate.getInt(0);
                    Timber.i("endDate %d", endDate);
                    maxDate.close();



                    Cursor minDate = getActivity().getContentResolver().query(DBContract.TimeTableEntry.minDate(), new String[]{DBContract.TimeTableEntry.TABLE_NAME}, "MAX(" + DBContract.TimeTableEntry.COLUMN_DATE + ")", null, null);

                    minDate.moveToFirst();
                    startDate = minDate.getInt(0);
                    Timber.i("startDate %d", startDate);
                    minDate.close();

                    if(todayDate>endDate) endDate=todayDate;

                    if(todayDate<startDate) startDate=todayDate;

                    horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.datePicker)
                            .startDate(getDateFromInt(startDate))
                            .endDate(getDateFromInt(endDate))
                            .build();

                    Timber.i("start %d end %d",startDate,endDate);
                }
            }

            scheduleAdapter = new ScheduleAdapter(getActivity(), null);
            mContext=this.getContext();
            //fetch day to set first screen to the upcoming time table
            c = Calendar.getInstance();
            selectedDate = c.get(Calendar.YEAR) * 10000 + c.get(Calendar.MONTH) * 100 + c.get(Calendar.DATE);
            if(horizontalCalendar!=null){
                horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
                    @Override
                    public void onDateSelected(Date date, int position) {
                        Timber.i("%d %d %d ", date.getYear(), date.getMonth(), date.getDate());
                        selectedDate = Utils.getDateInt(startDate,position);
                        mListener.onFragmentInteraction(Utils.getDay(startDate,position)+"'s Schedule");

                        restartLoad();
                    }
                });
            } else {
                horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.datePicker)
                        .startDate(getDateFromInt(todayDate))
                        .endDate(getDateFromInt(todayDate))
                        .build();
                horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
                    @Override
                    public void onDateSelected(Date date, int position) {
                        Timber.i("%d %d %d ", date.getYear(), date.getMonth(), date.getDate());
                        selectedDate = Utils.getDateInt(startDate,position);
                        mListener.onFragmentInteraction(Utils.getDay(startDate,position)+"'s Schedule");

                        restartLoad();
                    }
                });
            }
            getLoaderManager().initLoader(1, null, this);

            ListView scheduleList = rootView.findViewById(R.id.scheduleList);
            scheduleList.setAdapter(scheduleAdapter);

            scheduleList.setEmptyView(rootView.findViewById(R.id.empty));


            return rootView;

        }

        private void restartLoad(){
                getLoaderManager().restartLoader(1, null,  this);

        }
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                Timber.i("loader created %d", selectedDate);
                return new CursorLoader(getActivity(),
                        DBContract.TimeTableEntry.buildDateTableUri(selectedDate), null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                scheduleAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
                scheduleAdapter.swapCursor(null);
        }

        public interface OnFragmentInteractionListener {
                void onFragmentInteraction(String title);
        }

        public Date getDateFromInt(int intDate)  {
                int day = intDate % 100;
                int month = (intDate % 10000 - day) / 100 - 1;    // -1 for error in lib
                int year = (intDate - month * 100 - day) / 10000;
        return new Date(year, month, day);
        }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onMessageEvent(MainActivity.MessageEvent event){

        Timber.i(event.getMessage());

        switch (event.getMessage()){
            case "reload": {

                getLoaderManager().initLoader(1, null, this);

                break;

            }
            default:{
                Toast.makeText(getActivity(),event.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    public static class MessageEvent{

        public String message;

        public  MessageEvent(String message){
            this.message=message;
        }

        public String getMessage(){
            return message;
        }

    }
}
