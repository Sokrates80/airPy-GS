package airpygs.aplink.messages;

import airpygs.aplink.ApLinkParams;
import airpygs.utils.TxSettings;
import airpygs.utils.TxSettingsFloat;

/**
 * Created by fabrizioscimia on 28/06/16.
 */

public class AplSaveTxSettings extends AplMessage {

    public AplSaveTxSettings(TxSettingsFloat settings) {

        super(3*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE));
        this.setMessageTypeID(ApLinkParams.AP_MESSAGE_SAVE_TX_SETTINGS);
        this.setMessageID(AplMessage.getRandomMessageID());
        this.setQCI(0);
        this.setFailSafe(0);
        this.setFlightMode(0);
        this.setPayloadLength(3*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE));
        this.setLastFragment(ApLinkParams.LAST_FRAGMENT_YES);

        //Generate Payload converting 3 array of float into a byte array

        byte[] myBuffer = new byte[3*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE)];

        //copying min values
        for (int i = 0; i < settings.NUM_CHANNELS; i++) {
            System.arraycopy(AplMessage.floatToByteArray(settings.getMinThresholds()[i]),0,myBuffer,i*(Float.SIZE/Byte.SIZE),(Float.SIZE/Byte.SIZE));
        }

        //copying max values
        for (int i = 0; i < settings.NUM_CHANNELS; i++) {
            System.arraycopy(AplMessage.floatToByteArray(settings.getMaxThresholds()[i]),0,myBuffer,
                    settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE) + i*(Float.SIZE/Byte.SIZE),
                    (Float.SIZE/Byte.SIZE)
            );
        }

        //copying center values
        for (int i = 0; i < settings.NUM_CHANNELS; i++) {
            System.arraycopy(AplMessage.floatToByteArray(settings.getCenterThresholds()[i]),0,myBuffer,
                    2*settings.NUM_CHANNELS*(Float.SIZE/Byte.SIZE) + i*(Float.SIZE/Byte.SIZE),
                    (Float.SIZE/Byte.SIZE)
            );
        }

        //Set Payload
        this.setPayload(myBuffer);

    }
}
