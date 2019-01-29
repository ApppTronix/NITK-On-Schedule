package com.apptronix.nitkonschedule.teacher.ui

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.core.content.FileProvider
import androidx.loader.content.Loader
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apptronix.nitkonschedule.FileUtils
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.model.Schedule
import com.apptronix.nitkonschedule.model.SingleMarkedStudent
import com.apptronix.nitkonschedule.model.User
import com.apptronix.nitkonschedule.teacher.adapter.MarkAttendanceAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import kotlinx.android.synthetic.teacher.activity_mark_students.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find
import org.jetbrains.anko.forEachChild
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MarkStudents : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> , ClickListener{

    var id: Int = 0
    lateinit var mCurrentPhotoPath: String

    lateinit var markAttendanceAdapter:MarkAttendanceAdapter
    lateinit var schedule:Schedule
    lateinit var camFab: FloatingActionButton
    var presntIDs=""
    var enrolledIDs=""
    val REQUEST_TAKE_PHOTO = 1
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
                schedule=Schedule(dateInt,course,timeInt,null,null)

                supportLoaderManager.initLoader(2,null,this)
                Timber.i("loading 2")

            } else {
                Timber.i("cursor 1 is empty")
            }
        } else {
            if(data!!.moveToFirst()){
                enrolledIDs = data.getString(data.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE_ENROLLED_IDS))

                val ids = enrolledIDs.dropLast(1).split(";")
                Timber.i("attd %s from %s",ids.get(0),enrolledIDs)
                markAttendanceAdapter=MarkAttendanceAdapter(this, ids,false, this)
                markAttendanceList.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                markAttendanceList.adapter=markAttendanceAdapter
                markAttendanceAdapter.notifyDataSetChanged()
            } else {
                Timber.i("cursor 2 is empty")
            }

        }

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

        if(loader.id==1){
            id=0
        } else {

        }
    }

    override fun onPositionClicked(position: Int,checked: Boolean) {
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.apptronix.nitkonschedule.teacher",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_students)

        title="Mark Attendance"

        camFab = findViewById(R.id.cam_fab)
        camFab.setOnClickListener({
            dispatchTakePictureIntent()
        })

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra("id", 0)
        Timber.i("%d is id",id)


        supportLoaderManager.initLoader(1, null, this)
    }

    fun upload(view: View){
        markAttendanceList.forEachChild {
            if(it.find<CheckBox>(R.id.checkBox).isChecked){
                presntIDs=presntIDs+it.find<TextView>(R.id.rollNumber).text+";"
            }
        }
        schedule.presentIDs=presntIDs
        val bundle = Bundle()

        bundle.putSerializable("parcel",schedule)

        Timber.i("uploading attendance")
        bundle.putString("content","Upload Attendance")
        val intent = Intent(this, InstantUploadService::class.java)
        intent.putExtra("bundle",bundle)
        startService(intent)
        finish()
    }




    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: file_paths for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_TAKE_PHOTO) {
            Timber.i("Camera intent returned, starting upload")
            upload(mCurrentPhotoPath)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun upload(path: String) {
        val bundle = Bundle()
        bundle.putString("content","Upload Attendance Image")
        bundle.putString("file_paths",path)
        bundle.putString("course",course)
        val serviceIntent = Intent(this,InstantUploadService::class.java)
        serviceIntent.putExtra("bundle",bundle)
        startService(serviceIntent)
    }

    private var user: User? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        Timber.i(event.message)

        when (event.message) {
            "FaceRecognitionComplete" -> {

                Toast.makeText(this, "Got results!!", Toast.LENGTH_LONG).show()

                markAttendanceAdapter=MarkAttendanceAdapter(this, event.attendanceList, this)
                markAttendanceList.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                markAttendanceList.adapter=markAttendanceAdapter
                markAttendanceAdapter.notifyDataSetChanged()



            }
            else -> {

                Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    class MessageEvent(var message: String, var attendanceList: List<SingleMarkedStudent>)

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}
