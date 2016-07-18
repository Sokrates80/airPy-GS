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

import airpygs.aplink.messages.AplSavePIDs;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This test is used to verify if the length of the payload.
 */
public class AplSavePIDsTest {


    @org.junit.Test
    public void testGetPayloadLength() throws Exception {

        float[] testArray = {0.1f, 0.2f, 0.3f, 0.4f};

        AplSavePIDs msg = new AplSavePIDs(testArray);

        assertEquals("Size of 4 float element is 16", 16, msg.getPayloadLength());

    }
}