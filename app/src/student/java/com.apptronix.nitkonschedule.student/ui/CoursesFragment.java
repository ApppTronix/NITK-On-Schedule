package com.apptronix.nitkonschedule.student.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.model.Assignment;
import com.apptronix.nitkonschedule.model.AssignmentResponse;
import com.apptronix.nitkonschedule.model.Course;
import com.apptronix.nitkonschedule.model.User;
import com.apptronix.nitkonschedule.rest.ApiClient;
import com.apptronix.nitkonschedule.student.adapter.CoursesAdapter;
import com.apptronix.nitkonschedule.student.adapter.TestsAdapter;
import com.apptronix.nitkonschedule.student.data.DBContract;
import com.apptronix.nitkonschedule.student.rest.ApiInterface;
import com.apptronix.nitkonschedule.student.service.UploadService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Maha Perriyava on 4/6/2018.
 */

public class CoursesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private CoursesFragment.OnFragmentInteractionListener mListener;
    CoursesAdapter coursesAdapter;
    User user;
    EditText input;

    public CoursesFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);
        ListView courseList = (ListView)rootView.findViewById(R.id.coursesListView);
        coursesAdapter = new CoursesAdapter(getActivity(),null);
        courseList.setAdapter(coursesAdapter);
        courseList.setEmptyView(rootView.findViewById(R.id.empty));
        getLoaderManager().initLoader(0,null,this);
        FloatingActionButton fab = rootView.findViewById(R.id.enrollCourse);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Enroll Course");

                // Setting Dialog Message
                alertDialog.setMessage("Enter Course code");
                input = new EditText(getActivity());
                input.setGravity(Gravity.CENTER);
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.ic_school_black_24dp);

                alertDialog.setPositiveButton("ENROLL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                // Write your code here to execute after dialog

                                Bundle bundle = new Bundle();
                                bundle.putString("content","enrollCourse");
                                bundle.putSerializable("parcel", new Course(input.getText().toString()));
                                Intent intent = new Intent(getContext(), UploadService.class);
                                intent.putExtra("bundle",bundle);
                                Timber.i("starting service");
                                getActivity().startService(intent);

                            }
                        });
                alertDialog.setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });

                // closed

                // Showing Alert Message
                alertDialog.show();

            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TestsFragment.OnFragmentInteractionListener) {
            mListener = (CoursesFragment.OnFragmentInteractionListener) context;
            mListener.onFragmentInteraction("Courses ");
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
