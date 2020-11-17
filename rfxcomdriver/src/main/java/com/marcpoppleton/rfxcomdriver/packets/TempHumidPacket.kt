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
package com.marcpoppleton.rfxcomdriver.packets

data class TempHumidPacket(val length: Int, val name: String): PacketType(length,name){

    override var sensorId=-1
    override var signalLevel=-999
    override var batteryLevel=-999
    var temperature:Double=-999.99
    var humidity:Int=0
    var humidityStatus:Byte=0


    var typeId:Byte = 0
    var sequenceNumber:Byte = 0


    override fun receive(data: ByteArray) {

        typeId = data[2]
        sequenceNumber = data[3]
        sensorId = data[4].toInt() and 0xFF shl 8 or (data[5].toInt() and 0xFF)

        temperature = (data[6].toInt() and 0x7F shl length or (data[7].toInt() and 0xFF)) * 0.1
        if (data[6].toInt() and 0x80 !== 0) {
            temperature = -temperature
        }
        humidity = data[8].toInt()
        humidityStatus = data[9]

        signalLevel = (data[10].toInt() and 0xF0 shr 4)
        batteryLevel = data[10].toInt() and 0x0F
    }

    override fun defaultValue(): String =  "${"%.1f".format(temperature)}°C / $humidity%"

    override fun toString()= "${super.toString()} : sensor ${type()} ($sensorId) temperature ${"%.1f".format(temperature)} humidity $humidity signalLevel $signalLevel batteryLevel $batteryLevel"

    override fun type():String{
        return when(typeId.toInt()){
            0x01 -> "THGN122/123, THGN132, THGR122/228/238/268"
            0x02 -> "THGR810, THGN800"
            0x03 -> "RTGR328"
            0x04 -> "THGR328"
            0x05 -> "WTGR800"
            0x06 -> "THGR918, THGRN228, THGN500"
            0x07 -> "TFA TS34C, Cresta"
            0x08 -> "WT260,WT260H,WT440H,WT450,WT450H"
            0x09 -> "Viking 02035,02038"
            else -> "Unknown sensor type"
        }
    }
}