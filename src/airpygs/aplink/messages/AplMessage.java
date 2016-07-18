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

package airpygs.aplink.messages;

import airpygs.aplink.ApLinkParams;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

public class AplMessage {

    //Header fields
    private int messageID;
    private int QCI;
    private int lastFragment;
    private int messageTypeID;
    private int failSafe;
    private int flightMode;
    private int payloadLength;
    private byte[] payload;

    public AplMessage() {
        messageID = -1;
        QCI = -1;
        lastFragment = -1;
        messageTypeID = -1;
        failSafe = -1;
        flightMode = -1;
        payloadLength = -1;
        payload = new byte[ApLinkParams.PAYLOAD_MAX_LENGTH];
    }

    public AplMessage(int messageLength) {
        messageID = -1;
        QCI = -1;
        lastFragment = -1;
        messageTypeID = -1;
        failSafe = -1;
        flightMode = -1;
        payloadLength = -1;
        payload = new byte[messageLength];
    }

    public static int getRandomMessageID() {
        return ThreadLocalRandom.current().nextInt(ApLinkParams.MESSAGE_ID_MIN, ApLinkParams.MESSAGE_ID_MAX + 1);
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static byte[] floatToByteArray(float value) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putFloat(value);
        return bytes;
    }

    public static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[Integer.SIZE/Byte.SIZE];
        ByteBuffer.wrap(bytes).putInt(value);
        return bytes;
    }

    public byte[] getBytes() {
        byte[] messageBytes = new byte[ApLinkParams.HEADER_LENGTH + this.getPayloadLength() + 1]; //Last Byte is EOF

        //Build the Header

        messageBytes[0] = (byte) ApLinkParams.START_BYTE ;
        messageBytes[1] = (byte) (this.getMessageID() >> 8);
        messageBytes[2] = (byte) (this.getMessageID());
        messageBytes[3] = (byte) (this.getLastFragment() + (this.getQCI() << 3));
        messageBytes[4] = (byte) (this.getMessageTypeID());
        messageBytes[5] = (byte) (0);
        messageBytes[6] = (byte) (0);
        messageBytes[7] = (byte) (0);
        messageBytes[8] = (byte) (0);
        messageBytes[9] = (byte) (0);
        messageBytes[10] = (byte) (0);
        messageBytes[11] = (byte) (this.getPayloadLength());

        //Add Payload
        for (int i = 0; i < this.getPayloadLength(); i++) {
            messageBytes[ApLinkParams.HEADER_LENGTH + i] = this.getPayload()[i];
        }

        //Add EOF (1st byte of the payload)
        messageBytes[ApLinkParams.HEADER_LENGTH + this.getPayloadLength()] = this.getPayload()[0];

        return  messageBytes;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getQCI() {
        return QCI;
    }

    public void setQCI(int QCI) {
        this.QCI = QCI;
    }

    public int getLastFragment() {
        return lastFragment;
    }

    public void setLastFragment(int lastFragment) {
        this.lastFragment = lastFragment;
    }

    public int getMessageTypeID() {
        return messageTypeID;
    }

    public void setMessageTypeID(int messageTypeID) {
        this.messageTypeID = messageTypeID;
    }

    public int getFailSafe() {
        return failSafe;
    }

    public void setFailSafe(int failSafe) {
        this.failSafe = failSafe;
    }

    public int getFlightMode() {
        return flightMode;
    }

    public void setFlightMode(int flightMode) {
        this.flightMode = flightMode;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public void setPayloadByte(int byteIndex, byte b) {
        payload[byteIndex] = b;
    }
}