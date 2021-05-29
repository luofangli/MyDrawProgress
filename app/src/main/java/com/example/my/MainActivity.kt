package com.example.my

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ValueAnimator.ofFloat(0f,1f).apply {
            duration = 1000
            addUpdateListener {
                val value = it.animatedValue as Float
                custom_progress.loadProgress = value
            }
        }.start()
    }
}