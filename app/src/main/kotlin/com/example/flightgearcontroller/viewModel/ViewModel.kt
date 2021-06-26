package com.example.flightgearcontroller.viewModel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.example.flightgearcontroller.model.Model


class ViewModel(model: Model) : BaseObservable() {

    var model: Model

    // bindable values to ui
    @Bindable
    val ip = MutableLiveData<String>()

    @Bindable
    val port = MutableLiveData<String>()

    @Bindable
    val throttleText = MutableLiveData<String>()

    @Bindable
    val rudderText = MutableLiveData<String>()

    init {
        // init default values
        ip.value = ""
        port.value = ""
        ip.value = "192.168.1.90"
        port.value = "14069"
        this.model = model
    }

    fun onConnectClick(): Boolean {
        model.connectToFlightGear(this.ip.value, this.port.value)
        return true
    }

    fun stopThread() {
        model.stopThread()
    }

    fun sendCommand(name: String, value: Float) {
        when(name) {
            "throttle" -> throttleText.value = value.toString()
            "rudder" -> rudderText.value = value.toString()
        }
        model.sendCommand(name, value)
    }
}