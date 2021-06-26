package com.example.flightgearcontroller.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.flightgearcontroller.R
import com.example.flightgearcontroller.databinding.ActivityMainBinding
import com.example.flightgearcontroller.model.Model
import com.example.flightgearcontroller.viewModel.ViewModel


class MainActivity : AppCompatActivity() {

    private var relativeLayoutJoystick: RelativeLayout? = null
    var joystick: JoystickView? = null
    var seekBars: SeekBarsView? = null
    var viewModel: ViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // data binding settings
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val model = Model()
        viewModel = ViewModel(model)
        binding.model = model
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.executePendingBindings()

        // create sliders object
        seekBars = SeekBarsView(this)
        // listen to values change of sliders
        seekBars!!.sendUpdateEvent = IChange { name, value ->
            viewModel!!.sendCommand(name, value)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // close connection
        viewModel?.stopThread()
    }

    override fun onStart() {
        super.onStart()
        relativeLayoutJoystick = findViewById<View>(R.id.relativeLayoutJoystick) as RelativeLayout

        // create joystick object
        relativeLayoutJoystick?.post {
            val layoutWidth = relativeLayoutJoystick!!.width
            val layoutHeight = relativeLayoutJoystick!!.height
            val squaredSize = layoutWidth.coerceAtMost(layoutHeight)
            val squaredSmallSize = (0.25f * squaredSize).toInt()
            val offset = (squaredSmallSize / 2f).toInt()
            joystick = JoystickView(applicationContext)
            joystick!!.setXAxisName("aileron")
            joystick!!.setYAxisName("elevator")
            joystick!!.setOffset(offset)
            joystick!!.setStickSize(squaredSmallSize, squaredSmallSize)
            joystick!!.setLayoutSize(squaredSize, squaredSize)
            joystick!!.setThreshold(0.2f)
            joystick!!.createJoystick(relativeLayoutJoystick)

            // listen to values change on joystick
            joystick!!.sendUpdateEvent = IChange { name, value ->
                viewModel!!.sendCommand(name, value)
            }

        }

        val connectButton: Button = findViewById<View>(R.id.button) as Button
        connectButton.setOnClickListener(
            View.OnClickListener {
                // activate view model connect method
                viewModel?.onConnectClick()
                // hide keyboard
                this.currentFocus?.let { v ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }

        )
    }

}

