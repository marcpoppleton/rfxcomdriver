RFXCom driver for Android Things [ ![Download](https://api.bintray.com/packages/marcpoppleton/Maven/com.marcpoppleton%3Arfxcomdriver/images/download.svg) ](https://bintray.com/marcpoppleton/Maven/com.marcpoppleton%3Awsepdkdriver/_latestVersion)
============================

This is the source code of a RFXCom USB Antenna driver library for Android Things.
This project is under Apache licence 2.0 and comes as is. It is NOT provided by RFXCom.

Introduction
-------------

Currently supported devices:

* thermometer sensors
* thermo-hygro sensors

Currently available features:

* receiving data

Usage
--------

Add the dependency to your application's build.gradle file:

```groovy
implementation 'com.marcpoppleton:rfxcomddriver:0.1.1'
```

In your application's code you can use an RFXCom USB Antenna device to read data from RF433 devices supported by the RFXCom device.

The library is straightforward to use. It relies on LiveData to provide data to your app. All you have to do is create an instance of RFXCom providing it the context. It will return a LiveData which can then be observed.

The library manages connection and disconnection of the USB device and will connect/disconnect accordingly to the observing activity's lifecycle.

The following example will write on the logcat all sensors data received by a RFXCom Antenna connected on any USB port of the device running Android Things.
```kotlin
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
```

Depencencies
--------

The library currently relies on the Java [USBSerial](https://github.com/felHR85/UsbSerial) library, this should be removed in a future version once a simplified Kotlin version is be done (we only need a small subset of the lib). This is work in progress!

License
--------

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
