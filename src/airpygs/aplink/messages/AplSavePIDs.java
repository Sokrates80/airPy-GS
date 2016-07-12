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
        super(pidData.length*(Float.SIZE/Byte.SIZE)); // encoding each value as double will result in 8*4 bytes payload
        this.setMessageTypeID(ApLinkParams.AP_MESSAGE_SET_PID);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(pidData.length*(Float.SIZE/Byte.SIZE));
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Generate Payload converting the 8 float into a byte array
        byte[] myBuffer = new byte[pidData.length*(Float.SIZE/Byte.SIZE)];

        for (int i = 0; i < pidData.length; i++) {
            System.arraycopy(AplMessage.floatToByteArray(pidData[i]),0,myBuffer,i*(Float.SIZE/Byte.SIZE),(Float.SIZE/Byte.SIZE));
        }

        //Set Payload
        this.setPayload(myBuffer);

    }
}
