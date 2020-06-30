package com.kaveh.slider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kaveh.sliderview.DeepProgressView
import com.kaveh.sliderview.StepperSlider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<StepperSlider>(R.id.stepper).stepper = 4
    }
}