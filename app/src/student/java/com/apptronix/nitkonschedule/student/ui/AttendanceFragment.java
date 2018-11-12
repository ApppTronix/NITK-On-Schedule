package com.apptronix.nitkonschedule.student.ui;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.student.adapter.CourseWithAttendanceAdapter;
import com.apptronix.nitkonschedule.student.data.DBContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    private AttendanceFragment.OnFragmentInteractionListener mListener;
    CourseWithAttendanceAdapter coursesAdapter;

    public AttendanceFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_coursewattendance, container, false);
        ListView courseList = (ListView)rootView.findViewById(R.id.coursewattdListView);

        courseList.setEmptyView(rootView.findViewById(R.id.empty));
        coursesAdapter = new CourseWithAttendanceAdapter(getActivity(),null);
        courseList.setAdapter(coursesAdapter);
        getLoaderManager().initLoader(0,null,this);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AttendanceFragment.OnFragmentInteractionListener) {
            mListener = (AttendanceFragment.OnFragmentInteractionListener) context;
            mListener.onFragmentInteraction("Attendance ");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                DBContract.CourseEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        coursesAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader loader) {
        coursesAdapter.swapCursor(null);
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(String token);
    }

}
