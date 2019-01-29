package com.apptronix.nitkonschedule.teacher.ui.fragments

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.teacher.adapter.CoursesAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.ui.NewCourseActivity
import com.apptronix.nitkonschedule.teacher.ui.ResourcesActivity
import kotlinx.android.synthetic.teacher.fragment_courses.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class CoursesFragment : Fragment() , LoaderManager.LoaderCallbacks<Cursor> {

    override fun onLoaderReset(loader: Loader<Cursor>) {
        coursesAdapter.swapCursor(null)
    }

    private var mListener: OnFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments !=
                null) {

        }
    }

    lateinit var  cursor: Cursor
    lateinit var coursesAdapter: CoursesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment\

        val rootView = inflater!!.inflate(R.layout.fragment_courses, container, false)
        val coursesList = rootView.findViewById<View>(R.id.coursesListView) as ListView

        coursesAdapter= CoursesAdapter(activity,null)
        coursesList.adapter = coursesAdapter
        loaderManager.initLoader(0, null, this)

        coursesList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            cursor!!.moveToPosition(position)
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.pick_action)
                    .setItems(R.array.course_actions_array) { dialog, which ->
                        Timber.i("touch pos %d", which)
                        when (which) {
                            0 -> {//resources
                                val intent = Intent(activity, ResourcesActivity::class.java)
                                intent.putExtra("course", cursor.getString(cursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE)))
                                startActivity(intent)
                            }
                            1 -> {//tests

                            }
                            2 -> {//assignments
                            }
                        }
                    }
            val dialog = builder.create()
            dialog.show()
        }
        rootView.addCourse.setOnClickListener {
            val intent = Intent(activity, NewCourseActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
            mListener!!.onFragmentInteraction("Courses")
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        return CursorLoader(activity!!,
                DBContract.CourseEntry.CONTENT_URI, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        cursor = data
        coursesAdapter.swapCursor(data)
    }

    // TODO: Update argument type and name
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(title: String)

        fun onFragmentChange(code: String, title: String)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        Timber.i(event.message)

        when (event.message) {
            "reload" -> {

                loaderManager.restartLoader(0,null,this)

            }
        }
    }

    class MessageEvent(var message: String)

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}