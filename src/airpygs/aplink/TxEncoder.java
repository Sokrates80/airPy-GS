package airpygs.aplink;

import airpygs.aplink.messages.*;

/**
 * Created by fabrizioscimia on 13/03/16.
 */
public class TxEncoder {

    private serialHandler serial;

    public TxEncoder(serialHandler s) {
        serial = s;
    }

    public void enableMessage(int messageTypeId) {
        serial.writeBytes((new AplEnableMessage(messageTypeId).getBytes()));
        System.out.println("Sent Enable Request for Message Type " + messageTypeId);
    }

    public void disableMessage(int messageTypeId) {
        serial.writeBytes((new AplDisableMessage(messageTypeId).getBytes()));
        System.out.println("Sent Disable Request for Message Type " + messageTypeId);
    }

    public void enableEscCalibration() {
        serial.writeBytes((new AplEnableEscCalibration().getBytes()));
        System.out.println("Sent Enable Esc Calbration Request");
    }

    public void getCurrentPIDs() {
        serial.writeBytes((new AplLoadPIDs().getBytes()));
        System.out.println("Sent Current PID Settings Request");
    }

    public void savePidSettings(float[] pids) {
        serial.writeBytes((new AplSavePIDs(pids)).getBytes());
        System.out.println("Sent Save PIDs Request");
    }

    public void gyroCalibration(int option) {
        serial.writeBytes((new AplGyroCalibration(option)).getBytes());
        if (option == AplGyroCalibration.START_CALIBRATION) {
            System.out.println("Sent Giro Calibration Start Request");
        }

        if (option == AplGyroCalibration.STOP_CALIBRATION) {
            System.out.println("Sent Giro Calibration Stop Request");
        }
    }

    public void saveTxRxSettings(TxThresholds option, int[] thresholds) {

            if (option == TxThresholds.MIN) serial.writeBytes((new AplSaveTxSettings(
                    ApLinkParams.AP_MESSAGE_SAVE_TX_SETTINGS_MIN, thresholds).getBytes()));

            if (option == TxThresholds.CENTER) serial.writeBytes((new AplSaveTxSettings(
                    ApLinkParams.AP_MESSAGE_SAVE_TX_SETTINGS_CENTER, thresholds).getBytes()));

            if (option == TxThresholds.MAX) serial.writeBytes((new AplSaveTxSettings(
                    ApLinkParams.AP_MESSAGE_SAVE_TX_SETTINGS_MAX, thresholds).getBytes()));

        System.out.println("Sent Save TX Settings, option: " + option);
    }

}
