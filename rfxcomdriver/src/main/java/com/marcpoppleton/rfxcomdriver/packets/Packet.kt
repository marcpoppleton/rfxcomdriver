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

interface Packet{

    var sensorId:Int
    var signalLevel:Int
    var batteryLevel:Int
    fun receive(data:ByteArray)
    fun type():String
    fun defaultValue():String
}

abstract class PacketType(val l:Int, val n:String): Packet {
    override fun toString() = "Message type $n"
}

