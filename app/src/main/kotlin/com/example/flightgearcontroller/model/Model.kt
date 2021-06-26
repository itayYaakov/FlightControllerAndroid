package com.example.flightgearcontroller.model

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class Model : BaseObservable() {
    // bindable value - ui alter button text accordingly
    @Bindable
    public var isConnected = MutableLiveData<Boolean>()
    private var _isOn: Boolean = false
        set(value) {
            field = value
            // set isConnected value async
            isConnected.postValue(value)
        }

    private var thread: Thread? = null
    private var fg: Socket? = null
    private var out: PrintWriter? = null

    // holder for tasks
    var commandQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    fun connectToFlightGear(ip: String?, port: String?) {
        if (ip == null || port == null) return

        // if socket is connected and not closed - close the socket and return
        if (fg?.isConnected == true && fg?.isClosed == false) {
            stopThread()
            return
        }

        // create new thread
        if (thread == null || !thread!!.isAlive) {
            thread = Thread(Runnable {
                createConnection(ip, port)
                while (_isOn) {
                    commandQueue.take().run()
                }
                // _isOn is false -> end connection and close thread
                closeConnection()
                return@Runnable
            })
            thread!!.start()
        }
    }

    private fun createConnection(ip: String?, port: String) {
        try {
            fg = Socket(ip, port.toInt())
            out = PrintWriter(fg!!.getOutputStream(), true)
            _isOn = true
        } catch (e: InterruptedException) {
            Log.e("FG MODEL", e.toString())
            _isOn = false
        }
    }

    fun stopThread() {
        // set _isOn to false so the thread will end
        commandQueue.put(Runnable {
            _isOn = false
        })
    }

    fun closeConnection() {
        try {
            fg?.close()
            out?.close()
        } catch (e: Exception) {
            Log.e("FG MODEL", e.toString())
        }
    }

    // create flight gear string command and send it (using runnable task)
    fun sendCommand(name: String, value: Float) {
        if (!_isOn) return
        var command: String
        val valueClamped: Float
        if (name == "throttle") {
            command = "set /controls/engines/current-engine/%s %.2f\r\n"
            valueClamped = value.coerceAtLeast(0.0f).coerceAtMost(1.0f)
        } else {
            command = "set /controls/flight/%s %.2f\r\n"
            valueClamped = value.coerceAtLeast(-1.0f).coerceAtMost(1.0f)
        }
        // format string command
        command = command.format(name, valueClamped)
        // create new thread task
        commandQueue.put(Runnable {
            try {
                out?.print(command)
                out?.flush()
                Log.v("FG MODEL", command)
            } catch (e: Exception) {
                Log.e("FG MODEL", e.toString())
            }
        })
    }
}