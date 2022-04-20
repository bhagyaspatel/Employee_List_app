package com.example.companyemployee.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.example.companyemployee.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

//general function to be used in many other classes (so declared here)
fun createFile (context : Context, folder : String, ext : String) : File {
    val timeStamp : String = SimpleDateFormat ("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    val filesDir : File? = context.getExternalFilesDir(folder)
    val newFile = File (filesDir, "$timeStamp.$ext")
    newFile.createNewFile()
    return newFile
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //when drawer is open and user click back we want drawer to close not app to exit
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }
}