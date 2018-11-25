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
import com.apptronix.nitkonschedule.teacher.adapter.TestsAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import com.apptronix.nitkonschedule.teacher.ui.EditUploadTAActivity
import kotlinx.android.synthetic.teacher.fragment_tests.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class TestsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    override fun onLoaderReset(loader: Loader<Cursor>) {
        testsAdapter.swapCursor(null)
    }


    private var mListener: OnFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments !=
                null) {

        }
    }

    lateinit var  cursor: Cursor
    lateinit var testsAdapter: TestsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment\

        val rootView = inflater!!.inflate(R.layout.fragment_tests, container, false)
        val testsList = rootView.findViewById<View>(R.id.testsListView) as ListView

        testsAdapter=TestsAdapter(activity,null)
        testsList.adapter = testsAdapter
        loaderManager.initLoader(0, null, this)

        testsList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            cursor!!.moveToPosition(position)
            val builder = AlertDialog.Builder(this!!.activity!!)
            builder.setTitle(R.string.pick_action)
                    .setItems(R.array.actions_array) { dialog, which ->
                        Timber.i("touch pos %d", which)
                        when (which) {
                            0 -> {//edit
                                val intent = Intent(activity, EditUploadTAActivity::class.java)
                                intent.putExtra("title", "Edit Test")
                                intent.putExtra("id", cursor!!.getInt(cursor!!.getColumnIndex(DBContract.TestsEntry._ID)))
                                startActivity(intent)
                            }
                            1 -> {//grade
                            }
                            2 -> {//delete

                                val intent = Intent(activity, InstantUploadService::class.java)
                                val bundle = Bundle()
                                bundle.putString("content", "Delete Test")
                                bundle.putInt("id", cursor.getInt(cursor.getColumnIndex(DBContract.TestsEntry._ID)))
                                intent.putExtra("bundle",bundle)
                                activity!!.startService(intent)

                            }
                        }
                    }
            val dialog = builder.create()
            dialog.show()
        }
        rootView.uploadTest.setOnClickListener {
            val intent = Intent(activity, EditUploadTAActivity::class.java)
            intent.putExtra("title", "Upload Test")
            startActivity(intent)
        }

        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
            mListener!!.onFragmentInteraction("Tests ")
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
                DBContract.TestsEntry.CONTENT_URI, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        cursor = data
        testsAdapter.swapCursor(data)
    }

    // TODO: Update argument type and name
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(token: String)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }


    class MessageEvent(var message: String)

    @Subscribe(threadMode = ThreadMode.MAIN)

    fun onMessageEvent(event: MessageEvent) {

        Timber.i(event.message)
        when (event.message) {
            "reload" -> {

                loaderManager.restartLoader(1, null, this@TestsFragment)

            }
        }
    }

}// Required empty public constructor
