package com.example.flightgearcontroller.view

import android.app.Activity
import android.content.Context
import android.widget.SeekBar
import android.widget.TextView


class SlidersView() : SeekBar.OnSeekBarChangeListener {

    public var sendUpdateEvent: IChange? = null

    private var rudderText: TextView? = null
    private var throttleText: TextView? = null
    private var rudderSeekBar: SeekBar? = null
    private var throttleSeekBar: SeekBar? = null
    private var throttle: Float = 0f
    private var rudder: Float = 0f



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
        val progressF: Float = progress / 100f
        val progressS: String = progressF.toString()
        when (seekBar) {
            throttleSeekBar -> {
                throttle = progressF
                throttleText!!.text = progressS
                sendUpdateEvent?.onChange("throttle", progressF)
            }
            rudderSeekBar -> {
                rudder = progressF
                rudderText!!.text = progressS
                sendUpdateEvent?.onChange("rudder", progressF)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
}
