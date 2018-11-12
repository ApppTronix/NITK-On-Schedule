package com.apptronix.nitkonschedule.teacher.ui

import android.Manifest
import android.app.Activity
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.apptronix.nitkonschedule.FileUtils
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.teacher.adapter.ResourcesAdapter
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.service.InstantUploadService
import kotlinx.android.synthetic.teacher.activity_resources.*
import com.apptronix.nitkonschedule.rest.ApiClient
import com.apptronix.nitkonschedule.teacher.service.DownloadService
import java.io.File


class ResourcesActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>{
    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        return CursorLoader(this,DBContract.RepositoryEntry.buildRepositoryUri(course),null,null,null,null)
    }

    override fun onLoadFinished(p0: Loader<Cursor>?, p1: Cursor?) {
        cursor= p1!!
        resourcesAdapter.swapCursor(cursor)
    }

    override fun onLoaderReset(p0: Loader<Cursor>?) {
        resourcesAdapter.swapCursor(null)
    }

    lateinit var  cursor: Cursor
    lateinit var  uri: Uri
    lateinit var resourcesAdapter: ResourcesAdapter
    var course: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

        resourcesAdapter=ResourcesAdapter(this,null)
        resourcesListView.adapter = resourcesAdapter
        course = intent.extras.getString("course")
        title=course+" Resources"
        resourcesListView.setOnItemClickListener { parent, view, position, id ->
            val fileNameView = view.findViewById<TextView>(R.id.resourceName)
            if(fileNameView.getTag().equals("cloud")){

                val fileName = fileNameView.text
                val url = ApiClient.BASE_URL + "uploads/" + course + "/" + fileName
                var intent = Intent(this,DownloadService::class.java)
                intent.putExtra("url",url)
                intent.putExtra("course",course)
                intent.putExtra("fileName",fileName)
                startService(intent)


            } else {
                val locn = fileNameView.getTag() as String

                val myIntent = Intent(Intent.ACTION_VIEW)
                myIntent.data = Uri.fromFile(File(locn))
                val j = Intent.createChooser(myIntent, "Choose an application to open with:")
                startActivity(j)
            }
        }

        loaderManager.initLoader(0,null,this)
        uploadResourse.setOnClickListener {
            val getContentIntent = FileUtils.createGetContentIntent();
            val intent = Intent.createChooser (getContentIntent, "Select a file");
            startActivityForResult(intent, 1234);

        }
    }

    companion object {
        const val REQUEST_PERMISSION = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            1234 -> {
                if(resultCode== Activity.RESULT_OK){
                    val uri = data!!.data
                    this.uri=uri
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
                        return
                    }
                    upload(uri)
                }
            }
        }
    }

    fun upload(uri: Uri) {
        val path = FileUtils.getPath(this, uri)
        val bundle = Bundle()
        bundle.putString("content","Upload File")
        bundle.putString("file_paths",path)
        bundle.putString("course",course)
        val serviceIntent = Intent(this,InstantUploadService::class.java)
        serviceIntent.putExtra("bundle",bundle)
        startService(serviceIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                upload(uri)
            }
        }
    }

}
