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

public class ApLinkParams {

    //AirPy Link Protocol Header
    public static final short HEADER_LENGTH = 12;
    public static final short START_BYTE = 15;
    public static final short MESSAGE_ID_BYTE_1 = 1;
    public static final short MESSAGE_ID_BYTE_2 = 2;
    public static final short MESSAGE_ID_MAX = Short.MAX_VALUE;
    public static final short MESSAGE_ID_MIN = 5;
    public static final short QCI_AND_LAST_FRAGMENT_FLAG = 3;
    public static final short LAST_FRAGMENT_YES = 7;
    public static final short MESSAGE_TYPE_ID = 4;
    public static final short FAIL_SAFE_AND_FLIGHT_MODE = 10;
    public static final short PAYLOAD_LENGTH = 11;
    public static final short PAYLOAD_1ST_BYTE = 12;
    public static final short PAYLOAD_MAX_LENGTH = 1500; //TODO: retrieve it dynamically

    // AirPy Link Protocol messages type IDs
    public static final short AP_MESSAGE_HEARTBEAT = 10;
    public static final short AP_MESSAGE_RC_INFO = 20;
    public static final short AP_MESSAGE_IMU_STATUS = 30;
    public static final short AP_MESSAGE_ENABLE_MESSAGE = 40;
    public static final short AP_MESSAGE_DISABLE_MESSAGE = 50;
    public static final short AP_MESSAGE_ENABLE_ESC_CALIBRATION = 60;
    public static final short AP_MESSAGE_SET_PID = 70;
    public static final short AP_MESSAGE_GYRO_CALIBRATION = 80;
    public static final short AP_MESSAGE_LOAD_PIDS = 90;
    public static final short AP_MESSAGE_PID_SETTINGS = 100;
    public static final short AP_MESSAGE_SAVE_TX_SETTINGS = 110;

    // AirPy Link Protocol general parameters
    public static double MAX_RC_VALUE = 2047.0;  //Maximum value of ab RC channel (11 Bit)
    public static int MAX_RC_VALUE_INT = 2047;  //Maximum value of ab RC channel (11 Bit) integer
    public static int AP_MESSAGE_RC_INFO_NUM_CHANNELS = 18;

}
