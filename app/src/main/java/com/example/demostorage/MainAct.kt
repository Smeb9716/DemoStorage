package com.example.demostorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.act_main.*

class MainAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        tvOpenExternalStorage.setOnClickListener {
            startActivity(Intent(this,ExternalStorageAct::class.java))
        }
    }
}