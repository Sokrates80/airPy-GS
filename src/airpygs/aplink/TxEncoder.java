/*
 * airPyGS is a ground station software part of the airPy project (www.air-py.com).
 *
 * The MIT License (MIT)
 * Copyright (c) 2016 Fabrizio Scimia, fabrizio.scimia@gmail.com
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package airpygs.aplink;

import airpygs.aplink.messages.*;
import airpygs.utils.TxSettings;
import airpygs.utils.TxSettingsFloat;
import java.util.Arrays;

public class TxEncoder {

    private serialHandler serial;

    public TxEncoder(serialHandler s) {
        serial = s;
    }

    public void enableMessage(int messageTypeId) {
        serial.writeBytes((new AplEnableMessage(messageTypeId).getBytes()));
        System.out.println("Sent Enable Request for Message Type " + messageTypeId);
    }

    public void disableMessage(int messageTypeId) {
        serial.writeBytes((new AplDisableMessage(messageTypeId).getBytes()));
        System.out.println("Sent Disable Request for Message Type " + messageTypeId);
    }

    public void enableEscCalibration() {
        serial.writeBytes((new AplEnableEscCalibration().getBytes()));
        System.out.println("Sent Enable Esc Calbration Request");
    }

    public void getCurrentPIDs() {
        serial.writeBytes((new AplLoadPIDs().getBytes()));
        System.out.println("Sent Current PID Settings Request");
    }

    public void savePidSettings(float[] pids) {
        serial.writeBytes((new AplSavePIDs(pids)).getBytes());
        System.out.println("Sent Save PIDs Request");
    }

    public void gyroCalibration(int option) {
        serial.writeBytes((new AplGyroCalibration(option)).getBytes());
        if (option == AplGyroCalibration.START_CALIBRATION) {
            System.out.println("Sent Giro Calibration Start Request");
        }

        if (option == AplGyroCalibration.STOP_CALIBRATION) {
            System.out.println("Sent Giro Calibration Stop Request");
        }
    }

    public void saveTxSettings(TxSettingsFloat thresholds) {
        serial.writeBytes((new AplSaveTxSettings(thresholds).getBytes()));
        System.out.println("Sent Save TX Settings");
    }

}
