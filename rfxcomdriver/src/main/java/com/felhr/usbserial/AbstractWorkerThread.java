/**
 Software Name : UsbSerial
 SPDX-FileCopyrightText: Copyright (c) 2014 Felipe Herranz
 SPDX-License-Identifier: MIT License
 This software is distributed under the MIT licence.
 Author: Felipe Herranz et al.
 */
package com.felhr.usbserial;

abstract class AbstractWorkerThread extends Thread {
    boolean firstTime = true;
    private volatile boolean keep = true;
    private volatile Thread workingThread;

    void stopThread() {
        keep = false;
        if (this.workingThread != null) {
            this.workingThread.interrupt();
        }
    }

    public final void run() {
        if (!this.keep) {
            return;
        }
        this.workingThread = Thread.currentThread();
        while (this.keep && (!this.workingThread.isInterrupted())) {
            doRun();
        }
    }

    abstract void doRun();
}
