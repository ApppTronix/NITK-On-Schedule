package com.apptronix.nitkonschedule.teacher.ui


import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.Utils
import com.apptronix.nitkonschedule.teacher.model.Schedule
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import kotlinx.android.synthetic.teacher.activity_edit_upload_schedule.*
import timber.log.Timber
import java.util.*

class EditUploadSchedule : AppCompatActivity() {

    internal var id: Int = 0
    internal var cursor: Cursor? = null
    var dateInt: Int?=null
    var timeInt: Int?=null
    var titleText: String?=null
    val MONTHS = arrayOf( "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    lateinit var context:Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_upload_schedule)

        titleText = intent.getStringExtra("title")
        title = titleText
        id = intent.getIntExtra("id", 0)
        Timber.i("schedule id is %d",id)

        when (titleText) {
            "Edit Schedule" -> {
                editScheduleLoad(this.contentResolver.query(DBContract.TimeTableEntry.buildScheduleUri(id),
                        null, null, null, null))
            }
        }

        supportActionBar!!.setDisplayShowHomeEnabled(true)

    }

    private fun editScheduleLoad(cursor: Cursor?) {

        if(cursor!!.moveToFirst()){
            s_input_description.setText(cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_DESCRIPTION)))
            dateInt=cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_DATE))
            timeInt=cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_TIME))
            s_input_date.setText(Utils.convertToDate(dateInt!!))
            s_input_course.setText(cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_COURSE)))
            s_input_time.setText(Utils.setTime(timeInt!!))
            s_buttonUpload.text = "Update"
            s_input_course.isEnabled=false
            s_input_date.isEnabled=false
            s_input_time.isEnabled=false
        }

    }


    fun showCoursesList(v: View) {
        val newFragment = CoursePickerFragment()
        newFragment.show(supportFragmentManager, "coursePicker")
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)

    }


    private fun makeSchedule(): Schedule {
        val descText = s_input_description.text.toString()
        val courseText = s_input_course.text.toString()

        return Schedule(this.dateInt!!,courseText,this.timeInt!!,descText,"" )
    }

    fun showDatePickerDialog(v: View) {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            s_input_date.setText("" + dayOfMonth + " " + MONTHS[monthOfYear] + ", " + year)
            dateInt=year*10000+(monthOfYear+1)*100+dayOfMonth
            Timber.i("date %d",dateInt)
        }, year, month, day)
        dpd.show()

    }

    fun showTimePickerDialog(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val dpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, i, j ->
            s_input_time.setText(i.toString()+":"+j.toString())
            timeInt = i * 100 + j
            Timber.i("time %d",timeInt)
        }, hour, minute, false)
        dpd.show()

    }

    class CoursePickerFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val cursor = this.context!!.contentResolver.query(DBContract.CourseEntry.CONTENT_URI, null, null, null, null)


            val courses = ArrayList<String>()

            if(cursor.moveToFirst()){
                do { //load courses
                    courses.add(cursor.getString(cursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE)))
                } while (cursor.moveToNext())
            }


            var coursesArray: Array<String?>
            coursesArray = courses.toTypedArray()

            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(resources.getString(com.apptronix.nitkonschedule.R.string.select_course))
                    .setItems(coursesArray) {
                        dialog, which ->

                        activity!!.s_input_course.setText(coursesArray[which])
                    }

            return builder.create()
        }
    }


    fun upload(v: View) {

        val descText = s_input_description.text.toString()
        val courseText = s_input_course.text.toString()
        val dateText = s_input_date.text.toString()
        val timeText = s_input_time.text.toString()

        val details = arrayOf("",timeText,courseText,dateText) //don't remove that

        if (isValidInput(details)) {
            Timber.i("detail valid %s %s %s",timeText,courseText,dateText)
            val bundle = Bundle()

            val schedule =  Schedule(this.dateInt!!,courseText,this.timeInt!!,descText,"" )
            bundle.putSerializable("parcel",schedule)

            bundle.putString("content",titleText)
            val intent = Intent(this,InstantUploadService::class.java)
            intent.putExtra("bundle",bundle)
            startService(intent)

            finish()

        } else {
            Timber.i("detail invalid %s %s %s",timeText,courseText,dateText)
        }

    }


    private fun isValidInput(details: Array<String>): Boolean {

        var returnVal = true

        if (details[1] == null || "" == details[1]) {
            s_input_time.error = getString(R.string.timeError)
            returnVal = false
        }
        if (details[2] == null || "" == details[2]) {
            s_input_course.error = getString(R.string.courseCodeError)
            returnVal = false
        }
        if (details[3] == null || "" == details[3]) {
            s_input_date.error = getString(R.string.dateError)
            returnVal = false
        }

        return returnVal
    }


}
