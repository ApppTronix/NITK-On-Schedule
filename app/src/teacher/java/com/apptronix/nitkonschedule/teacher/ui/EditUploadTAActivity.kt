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
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.Utils
import com.apptronix.nitkonschedule.model.Assignment
import com.apptronix.nitkonschedule.model.Test
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import kotlinx.android.synthetic.teacher.activity_edit_upload_at.*
import timber.log.Timber
import java.util.*

val MONTHS = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")


class EditUploadTAActivity : AppCompatActivity() {

    var dateInt: Int? = null
    var timeInt: Int? = null
    var titleText: String? = null
    var weightageText: String? = null
    internal var id: Int = 0
    internal var cursor: Cursor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_upload_at)

        setSupportActionBar(uploadToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        titleText = intent.getStringExtra("title")
        title = titleText
        id = intent.getIntExtra("id", 0)

        input_weightage!!.addTextChangedListener(object : TextWatcher {
            internal var addedSuffix = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                // if the only text is the suffix
                val SUFFIX = "%"
                if (s.toString() == SUFFIX) {
                    input_weightage!!.setText("") // clear the text
                    return
                }

                // If there is text append on SUFFIX as long as it is not there
                // move cursor back before the sufix
                if (s.length > 0 && !s.toString().contains(SUFFIX) && s.toString() != SUFFIX) {
                    val text = s.toString() + SUFFIX
                    input_weightage!!.setText(text)
                    input_weightage!!.setSelection(text.length - SUFFIX.length)
                    addedSuffix = true // flip the addedSuffix flag to true
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) {
                    addedSuffix = false // reset the addedSuffix flag
                }
            }
        })

        when (titleText) {
            "Delete Assignment" -> {
                load(this.contentResolver.query(DBContract.AssignmentsEntry.buildAssignmentUri(id), null, null, null, null))
                deleteAssignment()
            }
            "Delete Test" -> {
                load(this.contentResolver.query(DBContract.TestsEntry.buildTestUri(id), null, null, null, null))
                deleteTest()
            }

            "Edit Assignment" -> {

                load(this.contentResolver.query(DBContract.AssignmentsEntry.buildAssignmentUri(id), null, null, null, null))
            }
            "Edit Test" -> {
                load(this.contentResolver.query(DBContract.TestsEntry.buildTestUri(id), null, null, null, null))
            }
        }

    }

    private fun load(cursor: Cursor?) {

        input_title.isEnabled=false
        cursor!!.moveToFirst()
        timeInt=cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_TIME))
        dateInt=cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_SUBMISSION_DATE))
        input_title.setText(cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_TITLE)))
        input_description.setText(cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_DESCRIPTION)))
        input_date.setText(Utils.convertToDate(dateInt!!))
        input_course.setText(cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_COURSE_CODE)))
        input_time.setText(Utils.setTime(timeInt!!))
        buttonUpload.text = "Update"

    }

    private fun deleteTest() {


        val bundle = Bundle()
        bundle.putSerializable("parcel", makeTest())
        bundle.putString("content", titleText)

        val intent = Intent(this, InstantUploadService::class.java)
        intent.putExtra("bundle",bundle)
        startService(intent)

        finish()
    }

    private fun makeTest(): Test {
        val titleTextInput = input_title.getText().toString()
        val descText = input_description.text.toString()
        val courseText = input_course.text.toString()
        val dateText = input_date.toString()
        val timeInt = Integer.parseInt(input_time.toString())
        weightageText = input_weightage!!.text.toString()
        weightageText= weightageText!!.substring(0, weightageText!!.length - 1)

        return Test(titleTextInput, descText, courseText, Integer.parseInt(dateText), Integer.parseInt(weightageText), timeInt)
    }

    private fun makeAssignment(): Assignment {
        val titleTextInput = input_title.text.toString()
        val descText = input_description.text.toString()
        val courseText = input_course.text.toString()

        val maxScoreText = input_max_score.text.toString()
        weightageText = input_weightage!!.text.toString()
        if(weightageText!!.length>0){
            weightageText= weightageText!!.substring(0, weightageText!!.length - 1)
        }

        return Assignment(titleTextInput, descText, courseText, dateInt!!, Integer.parseInt(weightageText), Integer.parseInt(maxScoreText))
    }

    private fun deleteAssignment() {

        this.contentResolver.delete(DBContract.AssignmentsEntry.buildAssignmentUri(id), null, null)

        Timber.i("delete assignment")
        val bundle = Bundle()
        bundle.putSerializable("parcel", makeAssignment())
        bundle.putString("content", titleText)
        val intent = Intent(this,InstantUploadService::class.java)
        intent.putExtra("bundle",bundle)
        startService(intent)
        finish()
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
            input_date.setText("" + dayOfMonth + " " + MONTHS[monthOfYear] + ", " + year)
            dateInt = year * 10000 + (monthOfYear + 1) * 100 + dayOfMonth
            Timber.i("date %d", dateInt)
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
            input_time.setText(i.toString() + ":" + j.toString())
            timeInt = i * 100 + j
            Timber.i("time %d", timeInt)
        }, hour, minute, false)
        dpd.show()

    }

    fun showCoursesList(v: View) {
        val newFragment = CoursePickerFragment()
        newFragment.show(supportFragmentManager, "coursePicker")
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)

    }

    class CoursePickerFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val cursor = this.context!!.contentResolver.query(DBContract.CourseEntry.CONTENT_URI, null, null, null, null)


            val courses = ArrayList<String>()

            if (cursor!!.moveToFirst()) {
                do { //load courses
                    courses.add(cursor.getString(cursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE)))
                } while (cursor.moveToNext())
            }

            var coursesArray: Array<String?>
            coursesArray = arrayOfNulls(courses.size)
            coursesArray = courses.toTypedArray()

            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(resources.getString(com.apptronix.nitkonschedule.R.string.select_course))
                    .setItems(coursesArray) { dialog, which -> activity!!.input_course.setText(coursesArray[which]) }
            return builder.create()
        }
    }

    fun upload(v: View) {

        val titleTextInput = input_title.getText().toString()
        val descText = input_description.text.toString()
        val courseText = input_course.text.toString()
        val dateText = input_date.toString()
        val timeText = input_time.toString()
        val maxScoreText = input_max_score.text.toString()
        if (null != input_weightage) {
            weightageText = input_weightage!!.text.toString()
            when {
                weightageText!!.contains("%",true) -> weightageText= weightageText!!.substring(0, weightageText!!.length - 1)
            }
        }

        val details = arrayOf(titleTextInput, descText, courseText, dateText, weightageText, timeText)

        if (isValidInput(details)) {
            val bundle = Bundle()
            if (titleTextInput.contains("Test")) {
                bundle.putSerializable("parcel", Test(titleTextInput, descText, courseText, Integer.parseInt(dateText), Integer.parseInt(weightageText), Integer.parseInt(timeText)))
            } else {
                bundle.putSerializable("parcel", Assignment(titleTextInput, descText, courseText, Integer.parseInt(dateText), Integer.parseInt(weightageText),Integer.parseInt(maxScoreText)))
            }

            bundle.putStringArray("details", details)
            bundle.putString("content", titleText)
            val intent = Intent(this,InstantUploadService::class.java)
            intent.putExtra("bundle",bundle)
            startService(intent)
            finish()

            finish()
        }

    }

    private fun isValidInput(details: Array<String?>): Boolean {

        var returnVal = true
        if (details[0] == null || "" == details[0]) {
            input_title.setError(getString(R.string.titleError))
            returnVal = false
        }
        if (details[1] == null || "" == details[1]) {
            input_description.error = getString(R.string.descError)
            returnVal = false
        }
        if (details[2] == null || "" == details[2]) {
            input_course.error = getString(R.string.courseCodeError)
            returnVal = false
        }
        if (details[3] == null || "" == details[3]) {
            input_date.error = getString(R.string.dateError)
            returnVal = false
        }
        if (details[4] == null || "" == details[4]) {
            input_weightage!!.error = getString(R.string.weightageError)
            returnVal = false
        }

        return returnVal
    }

}
