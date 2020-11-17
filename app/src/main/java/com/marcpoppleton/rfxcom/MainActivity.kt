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
package com.marcpoppleton.rfxcom

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.marcpoppleton.rfxcomdriver.RFXCom
import com.marcpoppleton.rfxcomdriver.packets.Packet

class MainActivity : AppCompatActivity() {

    private lateinit var rfx:RFXCom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rfx = RFXCom(this)
        rfx.observe(this, { packet: Packet? ->
            log("MainActivity got packet $packet")
        })
    }
}

fun log(msg: String) {
    Log.d(MainActivity::class.java.toString(), msg)
}