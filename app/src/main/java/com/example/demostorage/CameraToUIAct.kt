package com.example.demostorage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.act_camera_to_ui.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraToUIAct: AppCompatActivity() {
    private var pathToFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_camera_to_ui)

        tvCameraToUI.setOnClickListener {
            captureImageFromCamera()
        }
    }

    private var onActivityResultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val bitmap: Bitmap = BitmapFactory.decodeFile(pathToFile)
            imvCameraToUI.setImageBitmap(bitmap)
            savePhotoToExternalStorage(pathToFile!!,bitmap)
        }

    private fun savePhotoToExternalStorage(name: String, bitmap: Bitmap): Boolean {
        // dir
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // metadata
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        // save file
        return try {
            contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")

            true
        } catch (e: IOException) {
            println("debug: ${e.message}")
            false
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun captureImageFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = createPhotoFile()
            if (photoFile != null) {
                pathToFile = photoFile.absolutePath
                val photoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.demostorage.fileprovider",
                    photoFile
                )
                Log.d("uri", "PhotoUri is $photoUri")
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                onActivityResultLauncherCamera.launch(cameraIntent)
            }
        }
    }

    private fun createPhotoFile(): File? {
        val savingTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDirectory: File? = application.applicationContext
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var imageTaken: File? = null
        try {
            imageTaken = File.createTempFile(savingTime, ".jpg", storageDirectory)
            storageDirectory?.mkdir()
        } catch (ioE: IOException) {
            Log.d("myLog", "Exe: $ioE")
        }
        return imageTaken
    }
}