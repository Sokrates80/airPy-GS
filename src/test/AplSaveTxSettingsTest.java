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

package test;

import airpygs.aplink.messages.AplSaveTxSettings;
import airpygs.utils.TxSettings;
import airpygs.utils.TxSettingsFloat;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AplSaveTxSettingsTest {

    TxSettingsFloat txs = new TxSettingsFloat(3); // 3 Channels
    AplSaveTxSettings msg;

    private void initTest() {

        txs.setMinThreshold(0, 1f);
        txs.setMinThreshold(1, 3f);
        txs.setMinThreshold(2, 1f);
        txs.setMaxThreshold(0, 4f);
        txs.setMaxThreshold(1, 8f);
        txs.setMaxThreshold(2, 12f);
        txs.setCenterThreshold(0, 2f);
        txs.setCenterThreshold(1, 4f);
        txs.setCenterThreshold(2, 6f);

        msg = new AplSaveTxSettings(txs);
    }

    @org.junit.Test
    public void testGetPayloadLength() throws Exception {

        this.initTest();
        assertEquals("Size of 3 int * 3 arrays element is 36 bytes", 36, msg.getPayloadLength());


    }

    @org.junit.Test
    public void testGetPayloadValue() throws Exception {

        this.initTest();
        assertEquals("Value of Element (1,8) is 8", 8f, msg.getPayload()[19]);
    }


}
