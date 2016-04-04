package airpygs.aplink.messages;

import airpygs.aplink.ApLinkParams;

/**
 * Created by fabrizioscimia on 05/04/16.
 */
public class AplEnableEscCalibration extends AplMessage {

    public AplEnableEscCalibration() {
        super(2); // encoding msgTypeId as Short will result in 2 bytes payload
        this.setMessageTypeID(ApLinkParams.AP_MESSAGE_ENABLE_ESC_CALIBRATION);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(1);
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Set Payload
        this.setPayloadByte(0,(byte) (255));

    }
}
