/**
 Software Name : UsbSerial
 SPDX-FileCopyrightText: Copyright (c) 2014 Felipe Herranz
 SPDX-License-Identifier: MIT License
 This software is distributed under the MIT licence.
 Author: Felipe Herranz et al.
 */
package com.felhr.deviceids;

import static com.felhr.deviceids.Helpers.createTable;
import static com.felhr.deviceids.Helpers.createDevice;

public class CP2130Ids
{
    private static final long[] cp2130Devices = createTable(
            createDevice(0x10C4, 0x87a0)
    );

    public static boolean isDeviceSupported(int vendorId, int productId)
    {
        return Helpers.exists(cp2130Devices, vendorId, productId);
    }
}
