package airpygs.aplink.messages;

import airpygs.aplink.ApLinkParams;

/**
 * Created by fabrizioscimia on 10/04/16.
 *
 * This message is used to send the following new PIDs settings to airPy flight controller:
 *
 * Kp, Kd, Ki, Max Increment % (how much of the total thrust can be allocated by PIDs)
 *
 *
 */
public class AplSavePIDs extends AplMessage {

    public AplSavePIDs(float[] pidData) {
        super(32); // encoding each value as dobule will result in 4*4 bytes payload TODO: use sizeof float
        this.setMessageTypeID(ApLinkParams.AP_MESSAGE_SET_PID);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(32); //TODO: use sizeof float
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Generate Payload converting the 4 float into a byte array

        //TODO: use sizeof float
        byte[] myBuffer = new byte[32];

        for (int i = 0; i < pidData.length; i++) {
            System.arraycopy(AplMessage.toByteArray(pidData[i]),0,myBuffer,i*4,4);
        }

        //Set Payload
        this.setPayload(myBuffer);

    }
}
