package com.kaveh.slider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kaveh.sliderview.DeepProgressView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<DeepProgressView>(R.id.arc_progress_2).rotateEffectEnabled = false
    }
}