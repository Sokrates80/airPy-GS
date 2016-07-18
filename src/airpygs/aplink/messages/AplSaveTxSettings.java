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
import airpygs.utils.TxSettings;
import airpygs.utils.TxSettingsFloat;


public class AplSaveTxSettings extends AplMessage {

    public AplSaveTxSettings(TxSettingsFloat settings) {

        super(3*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE));
        this.setMessageTypeID(ApLinkParams.AP_MESSAGE_SAVE_TX_SETTINGS);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(3*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE));
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Generate Payload converting 3 array of float into a byte array

        byte[] myBuffer = new byte[3*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE)];

        //copying min values
        for (int i = 0; i < settings.NUM_CHANNELS; i++) {
            System.arraycopy(AplMessage.floatToByteArray(settings.getMinThresholds()[i]),0,myBuffer,i*(Float.SIZE/Byte.SIZE),(Float.SIZE/Byte.SIZE));
        }

        //copying max values
        for (int i = 0; i < settings.NUM_CHANNELS; i++) {
            System.arraycopy(AplMessage.floatToByteArray(settings.getMaxThresholds()[i]),0,myBuffer,
                    settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE) + i*(Float.SIZE/Byte.SIZE),
                    (Float.SIZE/Byte.SIZE)
            );
        }

        //copying center values
        for (int i = 0; i < settings.NUM_CHANNELS; i++) {
            System.arraycopy(AplMessage.floatToByteArray(settings.getCenterThresholds()[i]),0,myBuffer,
                    2*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE) + i*(Float.SIZE/Byte.SIZE),
                    (Float.SIZE/Byte.SIZE)
            );
        }

        //Set Payload
        this.setPayload(myBuffer);

    }
}
