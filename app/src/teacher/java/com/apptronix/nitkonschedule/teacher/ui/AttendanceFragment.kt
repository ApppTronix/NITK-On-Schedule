package com.apptronix.nitkonschedule.teacher.ui

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView

import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.Utils
import com.apptronix.nitkonschedule.teacher.adapter.AttendanceAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import timber.log.Timber

class AttendanceFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    override fun onLoaderReset(loader: Loader<Cursor>) {
        attdwcourseAdapter.swapCursor(null)
    }

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var attdwcourseAdapter: AttendanceAdapter
    lateinit var cursor:Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments !=
                null) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_attendance, container, false)
        val attdList = rootView.findViewById<View>(R.id.attendanceList) as ListView
        attdwcourseAdapter = AttendanceAdapter(activity, null)
        attdList.adapter = attdwcourseAdapter
        loaderManager.initLoader(0, null, this)
        attdList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            cursor.moveToPosition(position)
            val intent = Intent(activity, MarkStudents::class.java)
            intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry._ID)))
            intent.putExtra("course",cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_COURSE)))
            startActivity(intent)
        }
        Timber.i("created")
        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
            mListener!!.onFragmentInteraction("Attendance")
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        Timber.i("created loader %d",Utils.getTodayDate())

        return CursorLoader(activity!!,
                DBContract.TimeTableEntry.buildAttendanceURI(Utils.getTodayDate()), null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {

        Timber.i("loader returned %d",data.count)
        cursor=data
        attdwcourseAdapter.swapCursor(data)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(token: String)
    }
}// Required empty public constructor
