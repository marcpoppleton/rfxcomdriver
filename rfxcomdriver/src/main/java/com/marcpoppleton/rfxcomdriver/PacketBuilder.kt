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

import com.marcpoppleton.rfxcomdriver.packets.Packet
import com.marcpoppleton.rfxcomdriver.packets.TempHumidPacket
import com.marcpoppleton.rfxcomdriver.packets.TempPacket

class InvalidPacketLengthException(override var message:String): Exception(message)
class UnkownPacketTypeException(override var message:String): Exception(message)


fun parse(packetData:ByteArray): Packet?{
    val dataLength = (packetData.size - 1)
    if(packetData[0].toInt() !=dataLength) throw InvalidPacketLengthException("Packet unexpected length: expecting ${dataLength}, got ${packetData[1].toInt()}")

    val packetType = packetData[1].toInt()
    val packet = when(packetType){
        /*0x1 -> Packet(20,"Status")
        0x2 -> Packet(4, "TransmitAck")
        0x10 -> Packet(7, "LightingX10")
        0x11 -> Packet(11, "LightingHE")
        0x16 -> Packet(7, "Chime")*/
        0x50 -> TempPacket(8, "Temp")
        //0x51 -> Packet(8,"humid")
        0x52 -> TempHumidPacket(10, "TempHumid")
        /*0x55 -> Packet(11, "Rain")
        0x56 -> Packet(16, "Wind")
        0x5a -> Packet(17, "Power")*/
        else -> throw UnkownPacketTypeException("Packet type unknown: $packetType")
    }
    packet.receive(packetData)
    return packet
}