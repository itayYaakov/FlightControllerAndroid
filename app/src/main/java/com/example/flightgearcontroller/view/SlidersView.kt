package com.example.flightgearcontroller.view

import android.app.Activity
import android.content.Context
import android.widget.SeekBar
import android.widget.TextView


class SlidersView() : SeekBar.OnSeekBarChangeListener {

    private var rudderText: TextView? = null
    private var throttleText: TextView? = null
    private var rudderSeekBar: SeekBar? = null
    private var throttleSeekBar: SeekBar? = null


    constructor(context: Context?) : this() {
        val a: Activity? = context as Activity
        if (a != null) {
            rudderSeekBar = a.findViewById<SeekBar>(com.example.flightgearcontroller.R.id.seekBarRudder) as SeekBar
            throttleSeekBar = a.findViewById<SeekBar>(com.example.flightgearcontroller.R.id.seekBarThrottle) as SeekBar
            rudderText = a.findViewById<TextView>(com.example.flightgearcontroller.R.id.textRudderValue) as TextView
            throttleText = a.findViewById<TextView>(com.example.flightgearcontroller.R.id.textThrottleValue) as TextView

            rudderSeekBar!!.setOnSeekBarChangeListener(this)
            throttleSeekBar!!.setOnSeekBarChangeListener(this)
        }
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val progressF : String = (progress / 100f).toString()
        when (seekBar) {
            throttleSeekBar -> {
                throttleText!!.text = progressF
            }
            rudderSeekBar -> {
                rudderText!!.text = progressF
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
}
