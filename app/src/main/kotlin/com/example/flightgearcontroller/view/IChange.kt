package com.example.flightgearcontroller.view

// Interface for strategy design pattern
fun interface IChange {
    fun onChange(name: String, value: Float)
}
