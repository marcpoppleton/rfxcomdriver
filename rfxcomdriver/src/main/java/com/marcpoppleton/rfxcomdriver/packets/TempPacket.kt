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

data class TempPacket(val length: Int, val name: String): PacketType(length,name){

    override var sensorId=-1
    var temperature:Double=-999.99
    override var signalLevel=-999
    override var batteryLevel=-999

    var typeId:Byte = 0
    var sequenceNumber:Byte = 0


    override fun receive(data: ByteArray) {

        typeId = data[2]
        sequenceNumber = data[3]
        sensorId = data[4].toInt() and 0xFF shl 8 or (data[5].toInt() and 0xFF)

        temperature = (data[6].toInt() and 0x7F shl 8 or (data[7].toInt() and 0xFF)) * 0.1
        if (data[6].toInt() and 0x80 !== 0) {
            temperature = -temperature
        }

        signalLevel = (data[8].toInt() and 0xF0 shr 4)
        batteryLevel = data[8].toInt() and 0x0F
    }

    override fun defaultValue(): String = "${"%.1f".format(temperature)}°C"

    override fun toString()= "${super.toString()} : sensor ${type()} ($sensorId) temperature ${"%.1f".format(temperature)} signalLevel $signalLevel batteryLevel $batteryLevel"

    override fun type():String{
        return when(typeId.toInt()){
            0x01 -> "THR128/138, THC138"
            0x02 -> "THC238/268,THN132,THWR288,THRN122,THN122,AW129/131"
            0x03 -> "THWR800"
            0x04 -> "RTHN318"
            0x05 -> "La Crosse TX2, TX3, TX4, TX17"
            0x06 -> "TS15C"
            0x07 -> "Viking 02811"
            0x08 -> "La Crosse WS2300"
            0x09 -> "RUBiCSON"
            0x0a -> "TFA 30.3133"
            else -> "Unknown sensor type"
        }
    }
}