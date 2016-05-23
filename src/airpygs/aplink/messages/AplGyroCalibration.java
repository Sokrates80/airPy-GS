package airpygs.aplink.messages;

import airpygs.aplink.ApLinkParams;

/**
 * Created by fabrizioscimia on 24/04/16.
 */
public class AplGyroCalibration extends AplMessage {

    public static final int START_CALIBRATION = 10;
    public static final int STOP_CALIBRATION = 20;

    public AplGyroCalibration(int option) {
        super(2); // encoding msgTypeId as Short will result in 2 bytes payload
        this.setMessageTypeID(ApLinkParams.AP_MESSAGE_GYRO_CALIBRATION);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(1);
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Set Payload
        this.setPayloadByte(0,(byte) (option));

    }
}
