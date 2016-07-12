package airpygs.utils;

import airpygs.aplink.ApLinkParams;

/**
 * Created by fabrizioscimia on 06/07/16.
 */
public class TxSettings {

    private int[] minThresholds;
    private int[] maxThresholds;
    private int[] centerThresholds;
    public int NUM_CHANNELS;

    public TxSettings(int numChannels) {

        NUM_CHANNELS = numChannels;
        minThresholds = new int[numChannels];
        maxThresholds = new int[numChannels];
        centerThresholds = new int[numChannels];

        for (int i = 0; i < numChannels; i++) {
            minThresholds[i] = ApLinkParams.MAX_RC_VALUE_INT;
            maxThresholds[i] = 0;
            centerThresholds[i] = ApLinkParams.MAX_RC_VALUE_INT/2;
        }

    }

    public int getMinThreshold(int channelNum) {
        return minThresholds[channelNum];
    }

    public int getMaxThreshold(int channelNum) {
        return maxThresholds[channelNum];
    }

    public int getCenterThreshold(int channelNum) {
        return centerThresholds[channelNum];
    }

    public void setMinThreshold(int channelNum, int newThreshold) {
        minThresholds[channelNum] = newThreshold;
    }

    public void setMaxThreshold(int channelNum, int newThreshold) {
        maxThresholds[channelNum] = newThreshold;
    }

    public void setCenterThreshold(int channelNum, int newThreshold) {
        centerThresholds[channelNum] = newThreshold;
    }

    public int[] getMinThresholds() {
        return minThresholds;
    }

    public int[] getCenterThresholds() {
        return centerThresholds;
    }

    public int[] getMaxThresholds() {
        return maxThresholds;
    }
}
