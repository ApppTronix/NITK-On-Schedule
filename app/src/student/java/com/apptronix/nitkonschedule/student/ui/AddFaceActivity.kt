package com.apptronix.nitkonschedule.student.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import com.apptronix.nitkonschedule.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.apptronix.nitkonschedule.student.adapter.GridViewAdapter
import android.widget.GridView
import com.apptronix.nitkonschedule.student.adapter.ImageItem
import androidx.core.content.res.TypedArrayUtils.getResourceId
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.res.TypedArray
import android.graphics.Matrix
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.apptronix.nitkonschedule.student.service.UploadService
import timber.log.Timber
import kotlin.collections.ArrayList

class AddFaceActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var mCurrentPhotoPath: String
    lateinit var gridAdapter: GridViewAdapter
    lateinit var bitmap: Bitmap
    lateinit var imageItems:ArrayList<ImageItem>
    lateinit var imagePaths:ArrayList<String>


    val REQUEST_TAKE_PHOTO = 1
    var clickedPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_face)

        imageItems = ArrayList(9)
        imagePaths = ArrayList(9)

        Timber.i("add face started")

        title = "Add Face"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val gridView = findViewById<View>(R.id.gridView) as GridView
        val uploadBtn = findViewById<View>(R.id.uploadAllFaceImages) as AppCompatButton
        uploadBtn.setOnClickListener { view ->

            if(isValid()){
                val intent = Intent(this, UploadService::class.java)
                val bundle = Bundle()
                bundle.putString("content","uploadFaces")
                bundle.putStringArray("filePaths",imagePaths.toTypedArray())
                intent.putExtra("bundle",bundle)
                startService(intent)
            } else {
                Toast.makeText(this,"You got to take pictures from all angles before you can upload!", Toast.LENGTH_LONG).show()

            }



        }
        gridAdapter = GridViewAdapter(this, R.layout.grid_layout_item, getData())
        gridView.setAdapter(gridAdapter)
        gridView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Get the GridView selected/clicked item text
                val selectedItem = imageItems[position].title

                clickedPos=position
                dispatchTakePictureIntent()
                // Display the selected/clicked item text and position on TextView
                Timber.i("GridView item clicked : $selectedItem \nAt index position : $position")
            }
        }
    }

    private fun isValid():Boolean {
        try{
            for(i in 0 until 9){
                if(imagePaths[i].isNullOrEmpty()){
                    return false
                }
            }

        } catch (ex:IndexOutOfBoundsException){

            return false
        }

        return true
    }

    private fun getData(): ArrayList<ImageItem> {

        val imageTitles = resources.getStringArray(R.array.image_titles)
        for (i in 0 until 9) {
            when(i+1){
                1 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos1)
                }
                2 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos2)
                }
                3 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos3)
                }
                4 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos4)
                }
                5 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos5)
                }
                6 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos6)
                }
                7 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos7)
                }
                8 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos8)
                }
                9 -> {
                    bitmap = BitmapFactory.decodeResource(resources,R.drawable.pos9)
                }
            }


            imagePaths.add("")
            imageItems.add(ImageItem(bitmap, imageTitles[i]))
            Timber.i("getData loading image")
        }
        return imageItems
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

                    Timber.e("error creating file %s",ex.message)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            applicationContext,
                            "com.apptronix.nitkonschedule.student.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File.createTempFile(
                "JPEG_${"pos"+timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: file_paths for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
            Timber.i("current path is %s",mCurrentPhotoPath)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_TAKE_PHOTO ) {
            Timber.i("Camera intent returned, starting upload")
            //upload(mCurrentPhotoPath)

            val targetW = 100
            val targetH = 100

            // Get the dimensions of the bitmap
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // Determine how much to scale down the image
            val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true

            val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)

            val matrix = Matrix()
            matrix.postRotate(-90.0F)

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)

            val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

            imageItems[clickedPos].image = rotatedBitmap
            imagePaths.set(clickedPos,mCurrentPhotoPath)
            gridAdapter.setData(imageItems)


        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
