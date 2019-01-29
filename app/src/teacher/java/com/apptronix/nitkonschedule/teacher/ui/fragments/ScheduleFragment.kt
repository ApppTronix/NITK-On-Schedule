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
import android.widget.Toast
import androidx.loader.content.CursorLoader
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.Utils
import com.apptronix.nitkonschedule.teacher.adapter.ScheduleAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import com.apptronix.nitkonschedule.teacher.ui.EditUploadSchedule
import com.apptronix.nitkonschedule.teacher.ui.MarkStudents
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.HorizontalCalendarListener
import kotlinx.android.synthetic.teacher.fragment_schedule.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class ScheduleFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var mListener: OnFragmentInteractionListener? = null

    internal var selectedDate: Int = 0
    internal var startDate: Int = 0
    internal var endDate: Int = 0
    var scheduleAdapter: ScheduleAdapter? = null
    lateinit var c: Calendar
    lateinit var startC: Calendar
    var horizontalCalendar: HorizontalCalendar? = null
    lateinit var cursor:Cursor
    var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater!!.inflate(R.layout.fragment_schedule, container, false)

        val maxDate = activity!!.contentResolver.query(DBContract.TimeTableEntry.maxDate(), arrayOf(DBContract.TimeTableEntry.TABLE_NAME), "MAX(" + DBContract.TimeTableEntry.COLUMN_DATE + ")", null, null)
        if (maxDate != null) {

            if (maxDate.moveToFirst()) {

                endDate = maxDate.getInt(0)
                Timber.i("endDate %d", endDate)
                maxDate.close()

                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val todayDate=year*10000+(month+1)*100+day

                val minDate = activity!!.contentResolver.query(DBContract.TimeTableEntry.minDate(), arrayOf(DBContract.TimeTableEntry.TABLE_NAME), "MAX(" + DBContract.TimeTableEntry.COLUMN_DATE + ")", null, null)
                minDate!!.moveToFirst()
                startDate = minDate.getInt(0)

                if(endDate>todayDate) {

                    if(startDate>todayDate){

                        horizontalCalendar = HorizontalCalendar.Builder(rootView, R.id.datePicker)
                                .startDate(getDateFromInt(todayDate))
                                .endDate(getDateFromInt(endDate))
                                .build()

                        selectedDate = startDate
                    } else {

                        horizontalCalendar = HorizontalCalendar.Builder(rootView, R.id.datePicker)
                                .startDate(getDateFromInt(startDate))
                                .endDate(getDateFromInt(endDate))
                                .build()

                        selectedDate=todayDate
                    }

                } else {
                    horizontalCalendar = HorizontalCalendar.Builder(rootView, R.id.datePicker)
                            .startDate(getDateFromInt(startDate))
                            .endDate(getDateFromInt(todayDate))
                            .build()

                    selectedDate=todayDate
                }


                horizontalCalendar!!.selectDate(getDateFromInt(todayDate),true)
                Timber.i("start %d end %d today %d",startDate,endDate,todayDate)
            }

        }
        scheduleAdapter = ScheduleAdapter(activity, null)

        //fetch day to set first screen to the upcoming time table
        c = Calendar.getInstance()


        horizontalCalendar!!.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Date, position: Int) {
                if(!firstRun){
                    selectedDate = Utils.getDateInt(startDate,position)
                    Timber.i("%d is date ",selectedDate)
                    mListener!!.onFragmentInteraction(Utils.getDay(startDate,position) + "'s Time Table")
                    loaderManager.restartLoader(1, null, this@ScheduleFragment)
                }
                firstRun=false
            }
        }

        loaderManager.restartLoader(1, null, this@ScheduleFragment)

        //selectedDate = c.get(Calendar.YEAR) * 10000 + c.get(Calendar.MONTH) * 100 + c.get(Calendar.DATE)
        //loaderManager.initLoader(1, null, this)

        // horizontalCalendar.positionOfDate(new Date(selectedDate))

        rootView.scheduleList.emptyView=rootView.empty

        rootView.scheduleList.adapter = scheduleAdapter
        rootView.scheduleList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            cursor.moveToPosition(position)
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.pick_action)
                    .setItems(R.array.schedule_actions_array) { dialog, which ->
                        Timber.i("touch pos %d", which)
                        when (which) {
                            0 -> {//edit
                                val intent = Intent(activity, EditUploadSchedule::class.java)
                                intent.putExtra("title", "Edit Schedule")
                                intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry._ID)))
                                startActivity(intent)
                            }
                            1 -> {//Mark Attendance
                                val intent = Intent(activity, MarkStudents::class.java)
                                val presentIDs = cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_PRESENT_IDS))
                                if( null==presentIDs || presentIDs.isEmpty()){
                                    intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry._ID)))
                                    startActivity(intent)
                                } else {
                                    Timber.i("Attendance already marked")
                                    Toast.makeText(context,"Attendance already marked",Toast.LENGTH_LONG).show()
                                }
                            }
                            2 -> {//delete
                                val intent = Intent(activity, InstantUploadService::class.java)
                                val bundle = Bundle()
                                bundle.putString("content", "Delete Schedule")
                                bundle.putInt("id", cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry._ID)))
                                intent.putExtra("bundle",bundle)
                                activity!!.startService(intent)

                            }
                        }
                    }
            val dialog = builder.create()
            dialog.show()
        }

        rootView.addSchedule.setOnClickListener {
            val intent = Intent(context, EditUploadSchedule::class.java)
            intent.putExtra("title", "Upload Schedule")
            startActivity(intent)
        }

        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            val dayFormat = SimpleDateFormat("EEEE", Locale.US)
            val calendar = Calendar.getInstance()
            var weekDay = dayFormat.format(calendar.time)

            mListener = context
            mListener!!.onFragmentInteraction(weekDay + "'s Time Table")
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
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

                loaderManager.restartLoader(1, null, this@ScheduleFragment)

            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Timber.i("loader created %d", selectedDate)
        return CursorLoader(activity!!,
                DBContract.TimeTableEntry.buildDateTableUri(selectedDate), null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        data.moveToFirst()
        Timber.i("%d schedule count",data.count)
        cursor=data
        scheduleAdapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Timber.i("loader reset")

        scheduleAdapter!!.swapCursor(null)

    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(title: String)
    }

    fun getDateFromInt(intDate: Int): Date {
        val day = intDate % 100
        val month = (intDate % 10000 - day) / 100 - 1    // -1 for error in lib
        val year = (intDate - month * 100 - day) / 10000 - 1 // -1 for error in lib
        return Date(year, month, day)
    }
}
