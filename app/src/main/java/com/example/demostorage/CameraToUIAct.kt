package com.example.demostorage

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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