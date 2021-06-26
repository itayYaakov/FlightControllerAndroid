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
    @Bindable
    public var isConnected = MutableLiveData<Boolean>()
    private var _isOn: Boolean = false
        set(value) {
            field = value
            isConnected.postValue(value)
        }

    private var thread: Thread? = null
    private var fg: Socket? = null
    private var out: PrintWriter? = null
    var commandQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>(1)

    fun connectToFlightGear(ip: String?, port: String?) {
        if (ip == null || port == null) return

        if (fg?.isConnected == true && fg?.isClosed == false) {
            closeConnection()
            return
        }

        if (thread == null || !thread!!.isAlive) {
            thread = Thread(Runnable {
                try {
                    fg = Socket(ip, port.toInt())
                    out = PrintWriter(fg!!.getOutputStream(), true)
                    _isOn = true
                } catch (e: InterruptedException) {
                    Log.e("FG MODEL", e.toString())
                    return@Runnable
                }
                while (true) {
                    commandQueue.take().run()
                    if (!_isOn) {
                        try {
                            fg?.close()
                            out?.close()
                        } catch (e: Exception) {
                            Log.e("FG MODEL", e.toString())
                        }
                        return@Runnable
                    }
                }
            })
            thread!!.start()
        }
    }

    fun closeConnection() {
        commandQueue.put(Runnable {
            _isOn = false
        })
    }

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
        command = command.format(name, valueClamped)
        commandQueue.put(Runnable {
            try {
                out?.print(command)
                out?.flush()
                Log.w("FG_MODEL", "fg?.isConnected=" + fg?.isConnected + " Sending =" + command)
            } catch (e: InterruptedException) {
                Log.e("FG MODEL", e.toString())
            }
        })
    }
}