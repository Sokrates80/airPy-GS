package airpygs.aplink.messages;

import airpygs.aplink.ApLinkParams;

/**
 * Created by fabrizioscimia on 28/06/16.
 */

public class AplSaveTxSettings extends AplMessage {

    public AplSaveTxSettings(int option, int[] intData) {
        super(intData.length*(Integer.SIZE/Byte.SIZE)); // encoding each value as double will result in 8*4 bytes payload
        this.setMessageTypeID(option);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(intData.length*(Integer.SIZE/Byte.SIZE));
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Generate Payload converting the 8 float into a byte array
        byte[] myBuffer = new byte[intData.length*(Integer.SIZE/Byte.SIZE)];

        for (int i = 0; i < intData.length; i++) {
            System.arraycopy(AplMessage.intToByteArray(intData[i]),0,myBuffer,i*(Integer.SIZE/Byte.SIZE),(Integer.SIZE/Byte.SIZE));
        }

        //Set Payload
        this.setPayload(myBuffer);

    }
}
