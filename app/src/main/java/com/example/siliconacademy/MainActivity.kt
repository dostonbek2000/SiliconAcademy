package com.example.siliconacademy

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        windowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
//        window.statusBarColor = Color.TRANSPARENT

//        val w: Window = window
//        w.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )

    }

    private fun windowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win: Window = activity.window
        val windowParams: WindowManager.LayoutParams = win.attributes
        if (on) {
            windowParams.flags or bits
        } else {
            windowParams.flags and bits.inv()
        }

        win.attributes = windowParams
    }

}