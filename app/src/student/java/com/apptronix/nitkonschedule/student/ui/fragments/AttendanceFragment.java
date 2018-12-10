package com.apptronix.nitkonschedule.student.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.student.adapter.CourseWithAttendanceAdapter;
import com.apptronix.nitkonschedule.student.data.DBContract;
import com.apptronix.nitkonschedule.student.ui.AddFaceActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;


public class AttendanceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private AttendanceFragment.OnFragmentInteractionListener mListener;
    CourseWithAttendanceAdapter coursesAdapter;
    FloatingActionButton uploadFace;

    public AttendanceFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_coursewattendance, container, false);
        ListView courseList = rootView.findViewById(R.id.coursewattdListView);
        uploadFace = rootView.findViewById(R.id.uploadFace);
        uploadFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddFaceActivity.class));
            }
        });

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
