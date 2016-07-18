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

package airpygs.utils;

import airpygs.aplink.ApLinkParams;

public class TxSettingsFloat {

    private float[] minThresholds;
    private float[] maxThresholds;
    private float[] centerThresholds;
    public int NUM_CHANNELS;

    public TxSettingsFloat(int numChannels) {

        NUM_CHANNELS = numChannels;
        minThresholds = new float[numChannels];
        maxThresholds = new float[numChannels];
        centerThresholds = new float[numChannels];

        for (int i = 0; i < numChannels; i++) {
            minThresholds[i] = ApLinkParams.MAX_RC_VALUE_INT;
            maxThresholds[i] = 0;
            centerThresholds[i] = ApLinkParams.MAX_RC_VALUE_INT/2;
        }

    }

    public float getMinThreshold(int channelNum) {
        return minThresholds[channelNum];
    }

    public float getMaxThreshold(int channelNum) {
        return maxThresholds[channelNum];
    }

    public float getCenterThreshold(int channelNum) {
        return centerThresholds[channelNum];
    }

    public void setMinThreshold(int channelNum, float newThreshold) {
        minThresholds[channelNum] = newThreshold;
    }

    public void setMaxThreshold(int channelNum, float newThreshold) {
        maxThresholds[channelNum] = newThreshold;
    }

    public void setCenterThreshold(int channelNum, float newThreshold) {
        centerThresholds[channelNum] = newThreshold;
    }

    public float[] getMinThresholds() {
        return minThresholds;
    }

    public float[] getCenterThresholds() {
        return centerThresholds;
    }

    public float[] getMaxThresholds() {
        return maxThresholds;
    }
}
