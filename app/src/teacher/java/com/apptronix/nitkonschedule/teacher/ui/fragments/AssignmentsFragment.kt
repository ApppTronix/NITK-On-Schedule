package com.apptronix.nitkonschedule.teacher.ui.fragments

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.loader.content.CursorLoader
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.teacher.adapter.AssignmentsAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import com.apptronix.nitkonschedule.teacher.ui.EditUploadTAActivity
import kotlinx.android.synthetic.teacher.fragment_assignments.view.*
import timber.log.Timber

class AssignmentsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    override fun onLoaderReset(loader: Loader<Cursor>) {
        assgnAdapter.swapCursor(null)
    }

    private var mListener: OnFragmentInteractionListener? = null
    internal lateinit var assgnAdapter: AssignmentsAdapter
    internal lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments !=
                null) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_assignments, container, false)
        val assgnList = rootView.findViewById<View>(R.id.assgnListView) as ListView
        assgnAdapter = AssignmentsAdapter(activity, null)
        assgnList.adapter = assgnAdapter
        assgnList.setEmptyView(rootView.findViewById(R.id.empty))
        loaderManager.initLoader(0, null, this)
        assgnList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            cursor.moveToPosition(position)
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.pick_action)
                    .setItems(R.array.actions_array) { dialog, which ->
                        Timber.i("touch pos %d", which)
                        when (which) {
                            0 -> {//edit
                                val intent = Intent(activity, EditUploadTAActivity::class.java)
                                intent.putExtra("title", "Edit Assignment")
                                intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry._ID)))
                                startActivity(intent)
                            }
                            1 -> {//grade
                            }
                            2 -> {//delete
                                val intent = Intent(activity, InstantUploadService::class.java)
                                val bundle = Bundle()
                                bundle.putString("content", "Delete Assignment")
                                bundle.putInt("id", cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry._ID)))
                                intent.putExtra("bundle",bundle)
                                activity!!.startService(intent)

                            }
                        }
                    }
            val dialog = builder.create()
            dialog.show()
        }

        rootView.uploadAssignment.setOnClickListener {
            val intent = Intent(activity, EditUploadTAActivity::class.java)
            intent.putExtra("title", "Upload Assignment")
            startActivity(intent)
        }

        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
            mListener!!.onFragmentInteraction("Assignments ")
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
                DBContract.AssignmentsEntry.CONTENT_URI, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        cursor = data
        assgnAdapter.swapCursor(data)
    }


    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(token: String)
    }
}// Required empty public constructor
