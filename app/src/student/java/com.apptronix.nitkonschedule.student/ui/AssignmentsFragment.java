package com.apptronix.nitkonschedule.student.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.student.adapter.AssignmentsAdapter;
import com.apptronix.nitkonschedule.student.data.DBContract;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class AssignmentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private OnFragmentInteractionListener mListener;
    AssignmentsAdapter assgnAdapter;

    public AssignmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_assignments, container, false);
        ListView assgnList = (ListView)rootView.findViewById(R.id.assgnListView);
        assgnAdapter = new AssignmentsAdapter(getActivity(),null);
        assgnList.setAdapter(assgnAdapter);
        assgnList.setEmptyView(rootView.findViewById(R.id.empty));
        getLoaderManager().initLoader(0,null,this);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onFragmentInteraction("Assignments ");
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
                DBContract.AssignmentsEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        assgnAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String token);
    }
}
