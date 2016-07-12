package airpygs.utils;

import airpygs.aplink.ApLinkParams;

/**
 * Created by fabrizioscimia on 12/07/16.
 */
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
