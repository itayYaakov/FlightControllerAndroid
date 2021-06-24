package com.example.flightgearcontroller.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.flightgearcontroller.databinding.ActivityMainBinding
import com.example.flightgearcontroller.R
import com.example.flightgearcontroller.model.Model
import com.example.flightgearcontroller.viewModel.ViewModel
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar


class MainActivity : AppCompatActivity() {

    var relativeLayoutJoystick: RelativeLayout? = null
    var joystick: JoystickView? = null
    var sliders: SlidersView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val model = Model()
        val viewModel = ViewModel()
        binding.model = model
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.executePendingBindings()

        sliders = SlidersView(this)
    }

    override fun onStart() {
        super.onStart()
        relativeLayoutJoystick = findViewById<View>(R.id.relativeLayoutJoystick) as RelativeLayout

        relativeLayoutJoystick?.post {
            val layoutWidth = relativeLayoutJoystick!!.width
            val layoutHeight = relativeLayoutJoystick!!.height
            val squaredSize = Math.min(layoutWidth, layoutHeight).toInt()
            val squaredSmallSize = (0.25f * squaredSize).toInt()
            val offset = (squaredSmallSize / 2f).toInt()
            joystick = JoystickView(applicationContext)
            joystick!!.setOffset(offset)
            joystick!!.setStickSize(squaredSmallSize, squaredSmallSize)
            joystick!!.setLayoutSize(squaredSize, squaredSize)
            joystick!!.setMinimumDistance(20f)
            joystick!!.createJoystick(relativeLayoutJoystick)
        }
    }

}
