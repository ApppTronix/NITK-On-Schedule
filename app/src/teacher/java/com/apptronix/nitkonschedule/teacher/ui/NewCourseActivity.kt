package com.apptronix.nitkonschedule.teacher.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.model.Course
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import kotlinx.android.synthetic.teacher.activity_new_course.*
import timber.log.Timber

class NewCourseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_course)

        setTitle("Create Course")
        buttonUpload.setOnClickListener {
            upload()
        }
    }

    fun upload() {

        Timber.i("create course clicked")
        val descText = input_course_description.text.toString()
        val courseText = input_course_code.text.toString()

        val details = arrayOf("",descText,courseText) //don't remove that

        if (isValidInput(details)) {
            val bundle = Bundle()

            bundle.putSerializable("parcel", Course(courseText,descText,null,0))
            bundle.putString("content","Upload Course")

            var intent = Intent(this,InstantUploadService::class.java)
            intent.putExtra("bundle",bundle)
            startService(intent)
        }

    }

    private fun isValidInput(details: Array<String>): Boolean {

        var returnVal = true

        if (details[1] == null || "" == details[1]) {
            input_course_code.error = getString(R.string.courseCodeError)
            returnVal = false
        }
        if (details[2] == null || "" == details[2]) {
            input_course_description.error = getString(R.string.descError)
            returnVal = false
        }

        return returnVal
    }
}
