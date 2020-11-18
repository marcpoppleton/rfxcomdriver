/**
 Software Name : UsbSerial
 SPDX-FileCopyrightText: Copyright (c) 2014 Felipe Herranz
 SPDX-License-Identifier: MIT License
 This software is distributed under the MIT licence.
 Author: Felipe Herranz et al.
 */
package com.felhr.usbserial;

import java.util.List;


public interface SerialPortCallback {
    void onSerialPortsDetected(List<UsbSerialDevice> serialPorts);
}
