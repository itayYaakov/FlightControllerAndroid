package com.example.flightgearcontroller.viewModel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.example.flightgearcontroller.model.Model


class ViewModel(model: Model) : BaseObservable() {

    var model: Model

    @Bindable
    val ip = MutableLiveData<String>()

    @Bindable
    val port = MutableLiveData<String>()

    init {
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

    fun closeConnection() {
        model.closeConnection()
    }

    fun sendCommand(name: String, value: Float) {
        model.sendCommand(name, value)
    }
}