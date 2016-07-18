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

import airpygs.Controller;
import airpygs.aplink.messages.AplMessage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class RxDecoder extends Thread {

    //ApLink config
    Properties aplinkConfig;
    Controller apGui;

    private final long READING_INTERVAL;
    private boolean active;
    private boolean startByteFound;
    private int byteIndex;
    private int rcChannelIndex;
    private ApBuffer buffer;
    private byte tmpByte;

    //Message Specific
    private int tmpEOF;
    private AplMessage message;
    private int[] rcInfoMessage;
    private float[] rotations;
    private float[] pidSettings;
    private short[] motors;
    public StringProperty pitchString;
    public StringProperty rollString;
    public StringProperty yawString;
    public StringProperty gxString;
    public StringProperty gyString;
    public StringProperty gzString;

    //Counters
    private long validApLinkMessages;
    private long lostApLinkMessages;

    public RxDecoder(ApBuffer b, Controller gui) {

        aplinkConfig = aplinkConfigManager.getInstance().getConfig();
        message = new AplMessage();
        apGui = gui;
        READING_INTERVAL = Long.decode(aplinkConfig.getProperty("rxBufferReadingTimeInterval"));
        buffer = b;
        startByteFound = false;
        byteIndex = 0;
        rcChannelIndex = 0;
        validApLinkMessages = 0;
        lostApLinkMessages = 0;
        rcInfoMessage = new int[ApLinkParams.AP_MESSAGE_RC_INFO_NUM_CHANNELS];
        rotations = new float[6];
        pidSettings = new float[8];
        motors = new short[4];
        pitchString = new SimpleStringProperty("");
        rollString = new SimpleStringProperty("");
        yawString = new SimpleStringProperty("");
        gxString = new SimpleStringProperty("");
        gyString = new SimpleStringProperty("");
        gzString = new SimpleStringProperty("");
        apGui.getLabelPitch().textProperty().bind(pitchString);
        apGui.getLabelRoll().textProperty().bind(rollString);
        apGui.getLabelYaw().textProperty().bind(yawString);
        apGui.getLabelGPitch().textProperty().bind(gxString);
        apGui.getLabelGRoll().textProperty().bind(gyString);
        apGui.getLabelGYaw().textProperty().bind(gzString);
    }

    public void startRxDecoder(){
        active = true;
    }

    public void stopRxDecoder(){
        active = false;
    }

    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private void parseHeader() {
        switch (byteIndex) {

            case ApLinkParams.MESSAGE_ID_BYTE_1:            message.setMessageID((unsignedByteToInt(tmpByte) << 8));
                                                            break;

            case ApLinkParams.MESSAGE_ID_BYTE_2:            message.setMessageID(message.getMessageID() + unsignedByteToInt(tmpByte));
                                                            break;

            case ApLinkParams.QCI_AND_LAST_FRAGMENT_FLAG:   message.setQCI((unsignedByteToInt(tmpByte) & 0xF8)  >> 3);
                                                            message.setLastFragment((int)(tmpByte) & 0x07);
                                                            break;

            case ApLinkParams.MESSAGE_TYPE_ID:              message.setMessageTypeID((int) tmpByte);
                                                            break;

            case ApLinkParams.FAIL_SAFE_AND_FLIGHT_MODE:    message.setFailSafe(((int) (tmpByte) & 0xF0) >> 4);
                                                            message.setFlightMode((int)(tmpByte) & 0x0F);
                                                            break;

            case ApLinkParams.PAYLOAD_LENGTH:               message.setPayloadLength((int)tmpByte);
                                                            break;
        }

        byteIndex++;
    }

    private void loadPayload() {
        if (byteIndex == ApLinkParams.PAYLOAD_1ST_BYTE) {

            //The 1st byte of the payload is used to mark the end of the frame
            tmpEOF = tmpByte;
            message.setPayloadByte(byteIndex-ApLinkParams.HEADER_LENGTH,tmpByte);
            byteIndex++;

        } else if ((byteIndex == ApLinkParams.HEADER_LENGTH + message.getPayloadLength())) {

            if (tmpByte == tmpEOF) {
                //The whole message is valid. Update GUI
                validApLinkMessages++;
                System.out.println("Message Decoded -> MessageTypeID:" + message.getMessageTypeID() + " - QCI:" + message.getQCI() + " LastFragment:" + message.getLastFragment() + " PayloadLength: " + message.getPayloadLength());

                // Decode the message
                decodeApLinkMessage();

            } else {
                lostApLinkMessages++;
            }
            byteIndex = 0;
            startByteFound = false;
        } else {
            // keep loading payload bytes
            message.setPayloadByte(byteIndex-ApLinkParams.HEADER_LENGTH,tmpByte);
            byteIndex++;
        }

    }

    private void decodeApLinkMessage(){
        switch (message.getMessageTypeID()) {

            case ApLinkParams.AP_MESSAGE_HEARTBEAT:   decodeHeartBeat();
                                                         break;

            case ApLinkParams.AP_MESSAGE_RC_INFO:     decodeRcInfo();
                                                        break;

            case ApLinkParams.AP_MESSAGE_IMU_STATUS:  decodeImuStatus();
                                                        break;

            case ApLinkParams.AP_MESSAGE_PID_SETTINGS: decodePIDSettings();
                                                        break;
        }
    }

    public void run(){

            while (active) {
                try {
                    //Read a new byte from the Rx buffer
                    tmpByte = buffer.readRxBuffer();

                    if (startByteFound) {
                        if (byteIndex < ApLinkParams.HEADER_LENGTH) {
                            parseHeader();
                        } else {
                            loadPayload();
                        }

                    } else {
                        if ((short) tmpByte == ApLinkParams.START_BYTE) {
                            startByteFound = true;
                            byteIndex++;
                        } else {
                            // print byte to the gui
                            apGui.updateConsole(new String(new byte[]{ tmpByte }, StandardCharsets.US_ASCII));
                        }
                    }

                    sleep(READING_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }

    private void decodePIDSettings(){
        for (int i = 0; i < 8; i++) {
            byte[] bytes = {(message.getPayload())[i * 4],
                    (message.getPayload())[i * 4 + 1],
                    (message.getPayload())[i * 4 + 2],
                    (message.getPayload())[i * 4 + 3]
            };

            pidSettings[i] = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }

        apGui.updatePIDTextBox(pidSettings);
    }

    private void decodeHeartBeat(){
        apGui.setConnectLed(ConnectLed.TOGGLE);
    }

    //Extract Pitch, Roll, Yaw angle from the payload
    private void decodeImuStatus(){

        for (int i = 0; i < 6; i++) {
            byte[] bytes = {(message.getPayload())[i * 4],
                    (message.getPayload())[i * 4 + 1],
                    (message.getPayload())[i * 4 + 2],
                    (message.getPayload())[i * 4 + 3]
            };

            rotations[i] = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }

        for (int i = 0; i < 4; i++) {
            byte[] bytes_mot = {(message.getPayload())[i * 2 + 24],
                                (message.getPayload())[i * 2 + 25]
            };

            motors[i] = ByteBuffer.wrap(bytes_mot).order(ByteOrder.LITTLE_ENDIAN).getShort();
        }

        //Update rotation gui value
        Platform.runLater(() -> {
            //update labels
            pitchString.set(String.valueOf(rotations[0]));
            rollString.set(String.valueOf(rotations[1]));
            yawString.set(String.valueOf(rotations[2]));
            gxString.set(String.valueOf(rotations[3]));
            gyString.set(String.valueOf(rotations[4]));
            gzString.set(String.valueOf(rotations[5]));

            //update 3D model rotation
            apGui.updateModelRotations(rotations);

            //Update Charts
            apGui.updateAttitudeChart(rotations);
            apGui.updateMotorChart(motors);
        });

    }

    private void decodeRcInfo(){

        byte[] payload = message.getPayload();

        for (int i = 0; i < message.getPayloadLength(); i++) {
            // Each channel is encoded with 2 bytes.
            if (i % 2 == 0) {
                rcInfoMessage[rcChannelIndex] = (unsignedByteToInt(payload[i]));
            } else {
                rcInfoMessage[rcChannelIndex] = rcInfoMessage[rcChannelIndex] + (unsignedByteToInt(payload[i])<<8);
                rcChannelIndex++;
            }

        }

        //System.out.println( "CH1: " + rcInfoMessage[0] + " - CH2: " + rcInfoMessage[1] + " - CH3: " + rcInfoMessage[2] + " - CH4: " + rcInfoMessage[3]);
        Platform.runLater(() -> {
                    apGui.updateRcBars(rcInfoMessage);
                    rcChannelIndex = 0;
                });

    }
}
