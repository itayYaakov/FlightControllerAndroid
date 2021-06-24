package com.example.flightgearcontroller.viewModel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData

class ViewModel : BaseObservable() {

    @Bindable
    val ip = MutableLiveData<String>()
    @Bindable
    val port = MutableLiveData<String>()

    val aileron = MutableLiveData<Float>()
    val throttle = MutableLiveData<Float>()
    val rudder = MutableLiveData<Float>()
    val elevator = MutableLiveData<Float>()

    init {
        ip.value = ""
        port.value = ""
        aileron.value = 0.0f
        throttle.value = 0.0f
        rudder.value = 0.0f
        elevator.value = 0.0f
    }
}