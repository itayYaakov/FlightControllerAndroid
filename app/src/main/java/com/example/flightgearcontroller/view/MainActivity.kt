package com.example.flightgearcontroller.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.flightgearcontroller.R


class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }

    var relativeLayoutJoystick: RelativeLayout? = null
    var inner: ImageView? = null
    var outer: ImageView? = null

    var joystick: JoystickView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        relativeLayoutJoystick = findViewById<View>(R.id.relativeLayoutJoystick) as RelativeLayout
////
////         joystick = Joystick(applicationContext, relativeLayoutJoystick!!, R.drawable.joystick_in)
//        joystick = JoystickView(applicationContext)
//        joystick!!.setOffset(80)
//        joystick!!.setStickSize(150, 150)
////        joystick!!.setLayoutSize(850, 850)
//        joystick!!.setLayoutSize(250, 250)
//        joystick!!.setMinimumDistance(20f)
//        joystick!!.createJoystick(relativeLayoutJoystick!!)

//         relativeLayoutJoystick!!.setOnTouchListener { arg0, arg1 -> joystick!!.drawStick(arg1)
//        //     if (arg1.action == MotionEvent.ACTION_DOWN || arg1.action == MotionEvent.ACTION_MOVE) {
//        //         val x = "X : " + joystick!!.getX().toString()
//        //         val y = "Y : " + joystick!!.getY().toString()
//        //         val angle = "Angle : " + joystick!!.getAngle().toString()
//        //         val distance = "Distance : " + joystick!!.getDistance().toString()
//        //     } else if (arg1.action == MotionEvent.ACTION_UP) {
//
//             }
//             true;
//         }
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
