package com.apptronix.nitkonschedule.teacher.ui

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.Utils
import com.apptronix.nitkonschedule.teacher.model.Schedule
import com.apptronix.nitkonschedule.teacher.adapter.ViewMarkedAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.teacher.activity_mark_students.*
import timber.log.Timber

class ViewMarkedStudentActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>  {

    var id: Int = 0

    lateinit var markAttendanceAdapter: ViewMarkedAdapter
    lateinit var schedule: Schedule
    lateinit var camFab: FloatingActionButton
    var presntIDs=""
    var presentIDs=""
    lateinit var course :String

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if(id==1){
            return CursorLoader(this,
                    DBContract.TimeTableEntry.buildScheduleUri(this.id), null, null, null, null)

        } else {
            return CursorLoader(this,
                    DBContract.CourseEntry.buildCourseUri(course), null, null, null, null)
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if(loader.id==1){
            if (data!!.moveToFirst()){
                val dateInt=data.getInt(data.getColumnIndex(DBContract.TimeTableEntry.COLUMN_DATE))
                val timeInt=data.getInt(data.getColumnIndex(DBContract.TimeTableEntry.COLUMN_TIME))
                course = data.getString(data.getColumnIndex(DBContract.TimeTableEntry.COLUMN_COURSE))
                title = course + " " + Utils.timeFromInt(timeInt)
                schedule= Schedule(dateInt,course,timeInt,null,null)

                presentIDs = data.getString(data.getColumnIndex(DBContract.TimeTableEntry.COLUMN_PRESENT_IDS))
                Timber.i("attd %s %s",presentIDs.dropLast(1).split(";")[0],presentIDs)
                markAttendanceAdapter= ViewMarkedAdapter(this, presentIDs.dropLast(1).split(";").toTypedArray(),false)
                markAttendanceList.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                markAttendanceList.adapter=markAttendanceAdapter
                markAttendanceAdapter.notifyDataSetChanged()

            } else {
                Timber.i("cursor 1 is empty")
            }
        }


    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

        if(loader.id==1){
            id=0
        } else {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_marked_student)

        title="Attendance"


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra("id", 0)
        Timber.i("%d is id",id)


        supportLoaderManager.initLoader(1, null, this)
    }


}
