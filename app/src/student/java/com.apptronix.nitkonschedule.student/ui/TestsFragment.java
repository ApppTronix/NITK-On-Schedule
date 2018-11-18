package com.apptronix.nitkonschedule.student.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.student.adapter.TestsAdapter;
import com.apptronix.nitkonschedule.student.data.DBContract;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class TestsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private OnFragmentInteractionListener mListener;
    TestsAdapter testsAdapter;

    public TestsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_tests, container, false);
        ListView testsList = (ListView)rootView.findViewById(R.id.testsListView);
        testsAdapter = new TestsAdapter(getActivity(),null);
        testsList.setEmptyView(rootView.findViewById(R.id.empty));
        testsList.setAdapter(testsAdapter);
        getLoaderManager().initLoader(0,null,this);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onFragmentInteraction("Tests ");
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
                DBContract.TestsEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        testsAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader loader) {
        testsAdapter.swapCursor(null);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String token);
    }
}
