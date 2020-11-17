/**
RFXCom Driver
Copyright (C) 2020 Marc Poppleton
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.marcpoppleton.rfxcomdriver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.lifecycle.LiveData
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import com.marcpoppleton.rfxcomdriver.packets.Packet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.HashMap


enum class AntennaeState {
    NOT_CONNECTED,
    AWAITING_CONNECTION,
    CONNECTED
}

class RFXCom(private val ctx:Context):LiveData<Packet>() {

    var antennaeState: AntennaeState = AntennaeState.NOT_CONNECTED
        private set
    // UART Configuration Parameters
    private val BAUD_RATE = 38400
    private val DATA_BITS = 8
    private val STOP_BITS = 1

    private val USB_VENDOR_ID        = 0x0403 // 1027 in decimal
    private val USB_PRODUCT_ID       = 0x6001 // 24577 in decimal


    private lateinit var serial: UsbSerialDevice
    private lateinit var connection: UsbDeviceConnection
    private lateinit var manager: UsbManager

    override fun onActive() {
        super.onActive()
        GlobalScope.launch {
            connect()
        }
    }

    override fun onInactive() {
        super.onInactive()
        disconnect()
    }

    private suspend fun connect(){
        registerUsbListeners()
        manager = ctx.getSystemService(Context.USB_SERVICE) as UsbManager

        while (antennaeState == AntennaeState.NOT_CONNECTED) {
            val deviceList: HashMap<String, UsbDevice> = manager.deviceList
            for (device in deviceList.values) {
                if ((device.vendorId == USB_VENDOR_ID) && (device.productId == USB_PRODUCT_ID)) {
                    antennaeState = startUsbConnection(device)
                }
            }
            delay(500)
        }
    }

    private fun disconnect(){
        stopUsbConnection()
        unRegisterUsbListeners()
    }

    private fun registerUsbListeners(){
        val disconnectFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        val connectFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        ctx.registerReceiver(usbDetachedReceiver, disconnectFilter)
        ctx.registerReceiver(usbAttachedReceiver, connectFilter)
    }

    private fun unRegisterUsbListeners(){
        ctx.unregisterReceiver(usbDetachedReceiver)
        ctx.unregisterReceiver(usbAttachedReceiver)

    }

    private fun startUsbConnection(device: UsbDevice): AntennaeState {
        antennaeState = AntennaeState.AWAITING_CONNECTION
        connection = manager.openDevice(device)
        serial = UsbSerialDevice.createUsbSerialDevice(device, connection)
        log( "waiting for bootloader delay to pass...")
        runBlocking {
            delay(3000)
        }
        log( "delay passed, setting up serial connection")
        if (serial.open()) {

            serial.setBaudRate(BAUD_RATE)
            serial.setDataBits(DATA_BITS)
            serial.setStopBits(STOP_BITS)
            serial.setParity(UsbSerialInterface.PARITY_NONE)
            serial.read(usbDataCallback)
            antennaeState = AntennaeState.CONNECTED
        }
        return antennaeState
    }

    @Throws(IOException::class)
    fun stopUsbConnection(): AntennaeState {
        try {
            if(serial.isOpen){
                serial.close()
            }
            connection.close()
        } catch (e: Exception) {
            log( "error when stopping USB connection : $e")
        }
        antennaeState = AntennaeState.NOT_CONNECTED
        return antennaeState
    }

    private val usbDataCallback = object : UsbSerialInterface.UsbReadCallback {
        var dataBuffer = ByteArray(Byte.MAX_VALUE.toInt())
        var msgLen = 0
        var index = 0
        var start_found = false
        var ignoreReceiveBuffer = false
        var len = -1

        override fun onReceivedData(data: ByteArray?) {
            data?.let {
                len = data.size
                if (ignoreReceiveBuffer) {
                    // any data already in receive buffer will be ignored
                    ignoreReceiveBuffer = false;
                    start_found = false;
                    if (index > 0) {
                    }
                }
                for (i in 0 until len) {
                    if (index > Byte.MAX_VALUE.toInt()) {
                        // too many bytes received, try to find new start
                        start_found = false
                    }
                    if (!start_found && data[i] > 0) {
                        start_found = true
                        index = 0
                        dataBuffer[index++] = data[i]
                        msgLen = data[i] + 1
                    } else if (start_found) {
                        dataBuffer[index++] = data[i]
                        if (index == msgLen) {
                            val msg = ByteArray(msgLen)
                            for (j in 0 until msgLen) {
                                msg[j] = dataBuffer[j]
                            }
                            try {
                                val packet: Packet? = parse(msg)
                                // Set the LiveData value to the current packet, posting it since we're running on a background thread
                                postValue(packet)
                            } catch (e: Exception) {
                                log( "something went wrong : $e")
                            }
                            // find new start
                            start_found = false
                        }
                    }
                }
            }
        }
    }

    private val usbAttachedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (device != null && device.vendorId == USB_VENDOR_ID && device.productId == USB_PRODUCT_ID) {
                    startUsbConnection(device)
                }
            }
        }
    }

    private val usbDetachedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (device != null && device.vendorId == USB_VENDOR_ID && device.productId == USB_PRODUCT_ID) {
                    stopUsbConnection()
                }
            }
        }
    }

}

fun log(msg: String) {
    Log.d(RFXCom::class.java.toString(), msg)
}

