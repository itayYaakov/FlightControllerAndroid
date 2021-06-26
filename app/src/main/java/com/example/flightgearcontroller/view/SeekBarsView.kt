package com.example.flightgearcontroller.view

import android.app.Activity
import android.content.Context
import android.widget.SeekBar
import android.widget.TextView


class SeekBarsView() : SeekBar.OnSeekBarChangeListener {

    public var sendUpdateEvent: IChange? = null

    private var rudderSeekBar: SeekBar? = null
    private var throttleSeekBar: SeekBar? = null

    constructor(context: Context?) : this() {
        val a: Activity? = context as Activity
        if (a != null) {
            rudderSeekBar = a.findViewById<SeekBar>(com.example.flightgearcontroller.R.id.seekBarRudder) as SeekBar
            throttleSeekBar = a.findViewById<SeekBar>(com.example.flightgearcontroller.R.id.seekBarThrottle) as SeekBar

            // set this class as event listener class for seekbars
            rudderSeekBar!!.setOnSeekBarChangeListener(this)
            throttleSeekBar!!.setOnSeekBarChangeListener(this)
        }
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        // convert value to float between -1.0/0-1.0
        val progressF: Float = progress / 100f
        // send value change update
        when (seekBar) {
            throttleSeekBar -> sendUpdateEvent?.onChange("throttle", progressF)
            rudderSeekBar -> sendUpdateEvent?.onChange("rudder", progressF)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
}
