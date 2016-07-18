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

/**
 *
 * This message is used to send the following new PIDs settings to airPy flight controller:
 *
 * Kp, Kd, Ki, Max Increment % (how much of the total thrust can be allocated by PIDs)
 *
 *
 */
public class AplSavePIDs extends AplMessage {

    public AplSavePIDs(float[] pidData) {
        super(pidData.length*(Float.SIZE/Byte.SIZE)); // encoding each value as double will result in 8*4 bytes payload
        this.setMessageTypeID(ApLinkParams.AP_MESSAGE_SET_PID);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(pidData.length*(Float.SIZE/Byte.SIZE));
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Generate Payload converting the 8 float into a byte array
        byte[] myBuffer = new byte[pidData.length*(Float.SIZE/Byte.SIZE)];

        for (int i = 0; i < pidData.length; i++) {
            System.arraycopy(AplMessage.floatToByteArray(pidData[i]),0,myBuffer,i*(Float.SIZE/Byte.SIZE),(Float.SIZE/Byte.SIZE));
        }

        //Set Payload
        this.setPayload(myBuffer);

    }
}
